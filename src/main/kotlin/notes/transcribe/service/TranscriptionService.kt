package notes.transcribe.service

import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import notes.transcribe.domain.Transcription
import notes.common.kafka.model.VoiceNoteTranscribed
import notes.common.kafka.model.VoiceNoteUploaded
import notes.common.s3.service.ObjectStorageService
import notes.transcribe.repository.TranscriptionRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.InputStream

@Service
class TranscriptionService(
    private val objectStorageService: ObjectStorageService,
    private val repository: TranscriptionRepository,
    private val kafkaTemplate: KafkaTemplate<String, VoiceNoteTranscribed>,
    private val asrService: ASRService,
    @Value("\${kafka.topics.voice-note-transcription}") val transcriptionTopic: String,
    @Value("\${minio.bucket-name}") private val minioBucketName: String
) {

    private val log = LoggerFactory.getLogger(javaClass)

    //TODO read carefully and fix the return types etc for all these methods

    fun transcribe(voiceNoteUploaded: VoiceNoteUploaded): ResponseEntity<String> {
        val transcribedText = asrService.downloadAndTranscribeAudioFile(voiceNoteUploaded)
        saveTranscription(voiceNoteUploaded, transcribedText)//TODO: start refactoring from this point - you should be saving the voicenote id to the transcription not just the filename
        return ResponseEntity.ok(transcribedText)
    }

    private fun saveTranscription(voiceNoteUploaded: VoiceNoteUploaded, transcribedText: String?): Transcription {

        val filename = "text-${System.currentTimeMillis()}-transcription.txt"
        // Upload to object storage
        objectStorageService.uploadTextString(minioBucketName, filename, transcribedText ?: "")
        // Save to database
        val savedTranscript = repository.save(
            Transcription(
                transcriptionFilename = filename,
                voiceNoteId = voiceNoteUploaded.id,
                audioFilename = voiceNoteUploaded.fileName//TODO handle this de-normalization of data
            )
        )
        // Publish to Kafka
        publishTranscriptionToKafka(transcribedText, savedTranscript)

        return savedTranscript
    }

    private fun publishTranscriptionToKafka(transcribedMessage: String?, transcription: Transcription) {

        val voiceNoteTranscribed = VoiceNoteTranscribed(
            id = transcription.id,
            transcribedFilename = transcription.transcriptionFilename,
            transcribedText = transcribedMessage ?: "",
            voiceNoteId = transcription.voiceNoteId,
            voiceNoteFilename = transcription.audioFilename
        )

        log.info("Transcription: {}", transcribedMessage)

        log.info("Sending message to Kafka {}", voiceNoteTranscribed)
        val message: Message<VoiceNoteTranscribed> = MessageBuilder
            .withPayload(voiceNoteTranscribed)
            .setHeader(KafkaHeaders.TOPIC, transcriptionTopic)
            //.setHeader("X-Custom-Header", "Custom header here")
            .build()
        kafkaTemplate.send(message)
        log.info("Message sent with success")
        //ResponseEntity.ok().build()

    }

    fun getTranscriptionsForFiles(recordingsForToday: List<String>): List<Transcription> {
        val transcriptions = repository.findAllByAudioFilenameIn(recordingsForToday)
        log.info("Transcriptions: {}", transcriptions)
        return transcriptions
    }

    fun getTranscription(filename: String): String {
        val transcription = repository.findByTranscriptionFilename(filename)
        return objectStorageService.getTextFileStream(
            transcription.transcriptionFilename, minioBucketName)

    }




}