package notes.voice.service

import io.minio.*
import notes.transcribe.service.TranscriptionService
import notes.kafka.model.VoiceNoteUploaded
import notes.voice.domain.VoiceNote
import notes.voice.repository.VoiceNoteRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Service
class VoiceNoteService (
    private val transcriptionService: TranscriptionService,
    private val repository: VoiceNoteRepository,
    private val minioClient: MinioClient,
    private val kafkaTemplate: KafkaTemplate<String, VoiceNoteUploaded>,
    @Value("\${kafka.topics.voice-note}") private val topic: String,
    @Value("\${minio.bucket-name}") private val bucketName: String
){
    private val log = LoggerFactory.getLogger(javaClass)

    //TODO https://github.com/nehanims/notes/issues/38#issue-2492961136
    fun processVoiceNote(file: MultipartFile): VoiceNote {
        val filename = "voice-note-${System.currentTimeMillis()}-${file.originalFilename}"
        val inputStream: InputStream = file.inputStream
        uploadToObjectStorage(filename, inputStream, file)
        val savedVoiceNote = saveVoiceNoteToDatabase(filename)
        produceKafkaMessage(savedVoiceNote)
        return savedVoiceNote
    }

    private fun uploadToObjectStorage(
        filename: String,
        inputStream: InputStream,
        file: MultipartFile
    ) {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(filename)
                .stream(inputStream, file.size, -1)
                .contentType(file.contentType)
                .build()
        )
    }

    private fun saveVoiceNoteToDatabase(filename:String): VoiceNote {
        val voiceNote = VoiceNote(
            fileName = filename,
            bucketName = bucketName,
        )
        return repository.save(voiceNote)
    }

    private fun produceKafkaMessage(savedVoiceNote: VoiceNote): ResponseEntity<Any> {
        val voiceNoteUploaded = VoiceNoteUploaded(
            id = savedVoiceNote.id,
            fileName = savedVoiceNote.fileName,
            bucketName = savedVoiceNote.bucketName,
            //created = ZonedDateTime.now()
        )
        return try {
            log.info("Uploaded voice note")
            log.info("Sending message to Kafka {}", voiceNoteUploaded)
            val message: Message<VoiceNoteUploaded> = MessageBuilder
                .withPayload(voiceNoteUploaded)
                .setHeader(KafkaHeaders.TOPIC, topic)
                //.setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplate.send(message)
            log.info("Message sent with success")
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            log.error("Exception: {}",e.message)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending message")
        }
    }

    fun getPast24HoursRecordings(): List<Pair<String,String>> {
        val files = minioClient.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName)
                .build()
        )

        val now = ZonedDateTime.now()
        val cutoffTime = now.minus(24, ChronoUnit.HOURS)
        val recordingsForToday = files.filter { it.get().lastModified().isAfter(cutoffTime) }
            .filter { it.get().objectName().endsWith(".webm") }
            .map { it.get().objectName() }

        val transcriptions= transcriptionService.getTranscriptionsForFiles(recordingsForToday)

        return transcriptions.map { it.audioFilename to it.transcriptionFilename }
    }

    fun getAudioFile(filename: String): ByteArray {
        val stream: InputStream = minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucketName)
                .`object`(filename)
                .build()
        )
        return stream.readAllBytes()

    }
}

