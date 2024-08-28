package notes.voice.service

import io.minio.*
import io.minio.http.Method
import notes.transcribe.service.TranscriptionService
import notes.voice.domain.VoiceNoteUploaded
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
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@Service
class VoiceNoteService (
    @Value("\${kafka.topics.voice-note}") val topic: String,
    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, VoiceNoteUploaded>
){
    @Autowired
    private lateinit var transcriptionService: TranscriptionService
    private val log = LoggerFactory.getLogger(javaClass)
    //TODO should all these be constructor injected?

    @Autowired
    private lateinit var minioClient: MinioClient

    @Autowired
    private lateinit var repository: VoiceNoteRepository

    @Value("\${minio.bucket-name}")
    private lateinit var bucketName: String

    fun uploadFile(file: MultipartFile, metadata: Map<String, String>): VoiceNote {
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
        //TODO remove this URL from the entity it is useless
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
        produceKafkaMessage(VoiceNoteUploaded("VoiceNote", voiceNote.filename))
        return voiceNote
    }

    fun produceKafkaMessage(voiceNoteUploaded: VoiceNoteUploaded): ResponseEntity<Any> {
        return try {
            log.info("Uploaded voice note")
            log.info("Sending message to Kafka {}", voiceNoteUploaded)
            val message: Message<VoiceNoteUploaded> = MessageBuilder
                .withPayload(voiceNoteUploaded)
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

    fun getTodaysRecordings(): List<Pair<String,String>> {

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

