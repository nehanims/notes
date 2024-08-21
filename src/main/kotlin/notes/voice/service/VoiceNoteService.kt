package notes.voice.service

import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.http.Method
import notes.voice.domain.TestMessage
import notes.voice.domain.VoiceNote
import notes.voice.repository.VoiceNoteRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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
import java.util.concurrent.TimeUnit

@Service
class VoiceNoteService (
    @Value("\${kafka.topics.test_topic}") val topic: String,
    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, Any>
){
    private val log = LoggerFactory.getLogger(javaClass)
    //TODO should all these be constructor injected?

    @Autowired
    private lateinit var minioClient: MinioClient

    @Autowired
    private lateinit var repository: VoiceNoteRepository

    @Value("\${minio.bucket-name}")
    private lateinit var bucketName: String

    fun uploadFile(file: MultipartFile, metadata: Map<String, String>): String {
        val filename = "${System.currentTimeMillis()}-${file.originalFilename}"
        val inputStream: InputStream = file.inputStream
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(filename)
                .stream(inputStream, file.size, -1)
                .contentType(file.contentType)
                .build()
        )
        val fileUrl = minioClient.getPresignedObjectUrl( GetPresignedObjectUrlArgs.builder()
            .method(Method.GET)
            .bucket(bucketName)
            .`object`(filename)
            .expiry(7, TimeUnit.DAYS)
            .build())

        val voiceNote = VoiceNote(
            filename = filename,
            url = fileUrl,
            length = metadata["length"]?.toLong() ?: 0,
            encoding = metadata["encoding"] ?: "unknown",
            channels = metadata["channels"]?.toInt() ?: 1,
            title = metadata["title"]
        )
        repository.save(voiceNote)
        //produceKafkaMessage(TestMessage("VoiceNote", voiceNote.url))//TODO  make sure all these operations are synchronous and transactional -- if any of the operations fail everything should be rolled back - BUT look more into whether that's the right approach, would you want partially commited state in the database if it is not written to kafka? or file uploaded if it fails to write to metadata store?
        produceKafkaMessage(TestMessage("VoiceNote", voiceNote.filename))
        return fileUrl
    }

    fun produceKafkaMessage(testMessage: TestMessage): ResponseEntity<Any> {
        return try {
            log.info("Uploaded voice note")
            log.info("Sending message to Kafka {}", testMessage)
            val message: Message<TestMessage> = MessageBuilder
                .withPayload(testMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplate.send(message)
            log.info("Message sent with success")
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            log.error("Exception: {}",e.message)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending message")
        }
    }
}

