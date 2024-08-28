package notes.transcribe.service

import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import notes.transcribe.domain.Transcription
import notes.kafka.model.VoiceNoteTranscribed
import notes.kafka.model.VoiceNoteUploaded
import notes.transcribe.repository.TranscriptionRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.*
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayInputStream
import java.io.InputStream

@Service
class TranscriptionService(
    @Value("\${kafka.topics.voice-note-transcription}") val transcriptionTopic: String,
    @Value("\${transcription.endpoint.host}") val transcriptionEndpoint: String,
    @Autowired private val minioClient: MinioClient,
    @Autowired private val repository: TranscriptionRepository,
    @Autowired private val kafkaTemplate: KafkaTemplate<String, VoiceNoteTranscribed>,
    @Value("\${minio.bucket-name}") private val minioBucketName: String
) {

    private val log = LoggerFactory.getLogger(javaClass)

    //TODO read carefully and fix the return types etc for all these methods

    fun transcribe(voiceNoteUploaded: VoiceNoteUploaded): ResponseEntity<String> {
        val transcribedText = downloadAndTranscribeAudioFile(voiceNoteUploaded)
        saveTranscription(voiceNoteUploaded.fileName, transcribedText)//TODO: start refactoring from this point - you should be saving the voicenote id to the transcription not just the filename
        return ResponseEntity.ok(transcribedText)
    }

    fun downloadAndTranscribeAudioFile(voiceNoteUploaded: VoiceNoteUploaded): String? {

        // Fetch the file from MinIO as InputStream
        val stream: InputStream = minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(voiceNoteUploaded.bucketName)
                .`object`(voiceNoteUploaded.fileName)
                .build()
        )

        // Convert InputStream to ByteArray
        val byteArray = stream.use { it.readBytes() }

        // Prepare the ByteArrayResource for the file
        val fileAsResource = object : ByteArrayResource(byteArray) {
            override fun getFilename(): String {
                return voiceNoteUploaded.fileName // Specify the filename
            }
        }

        // Prepare headers and the request body
        val headers = HttpHeaders().apply {
            contentType = MediaType.MULTIPART_FORM_DATA
        }

        val bodyBuilder = MultipartBodyBuilder()
        bodyBuilder.part("audio_file", fileAsResource)

        val requestEntity: HttpEntity<MultiValueMap<String, HttpEntity<*>>> = HttpEntity(bodyBuilder.build(), headers)

        // Send the file to the endpoint
        log.info("Sending request to ASR service")
        val restTemplate = RestTemplate()
        val response = restTemplate.postForEntity(
            "${transcriptionEndpoint}/asr?encode=true&task=transcribe&language=en&word_timestamps=false&output=txt",
            requestEntity,
            String::class.java
        )

        // Handle the response
        log.info("Received response: ${response.body}")

        return response.body
    }

    private fun saveTranscription(audioFileName: String, transcribedText: String?): Transcription {
        // Upload to minio
        val filename = "${System.currentTimeMillis()}-transcription.txt"
        uploadString(minioBucketName, filename, transcribedText ?: "")

        val transcription = Transcription(
            transcriptionFilename = filename,
            audioFilename = audioFileName
        )
        repository.save(transcription)
        publishTranscriptionToKafka(transcribedText, transcription)
        return transcription
    }


    private fun publishTranscriptionToKafka(transcribedMessage: String?, transcription: Transcription) {

        val voiceNoteTranscribed = VoiceNoteTranscribed(
            id = transcription.id,
            transcribedFilename = transcription.transcriptionFilename,
            transcribedText = transcribedMessage ?: "",
            voiceNoteFilename = transcription.audioFilename
        )

        log.info("Transcription: {}", transcribedMessage)

        log.info("Sending message to Kafka {}", voiceNoteTranscribed)
        val message: Message<VoiceNoteTranscribed> = MessageBuilder
            .withPayload(voiceNoteTranscribed)
            .setHeader(KafkaHeaders.TOPIC, transcriptionTopic)
            .setHeader("X-Custom-Header", "Custom header here")
            .build()
        kafkaTemplate.send(message)
        log.info("Message sent with success")
        //ResponseEntity.ok().build()

    }

    fun uploadString(bucketName: String, objectName: String, content: String) {

        log.info("Uploading string to MinIO")
        val byteArrayInputStream = ByteArrayInputStream(content.toByteArray())

        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectName)
                .stream(byteArrayInputStream, content.length.toLong(), -1)
                .contentType("text/plain")
                .build()
        )
        log.info("Uploaded string to MinIO")
    }

    fun getTranscriptionsForFiles(recordingsForToday: List<String>): List<Transcription> {
        val transcriptions = repository.findAllByAudioFilenameIn(recordingsForToday)
        log.info("Transcriptions: {}", transcriptions)
        return transcriptions
    }

    fun getTranscription(filename: String): String {
        val transcription = repository.findByTranscriptionFilename(filename)
        val stream: InputStream = minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(minioBucketName)
                .`object`(transcription.transcriptionFilename)
                .build()
        )
        return stream.bufferedReader().use { it.readText() }

    }


}