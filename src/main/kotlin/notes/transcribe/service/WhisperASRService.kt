package notes.transcribe.service

import io.minio.GetObjectArgs
import io.minio.MinioClient
import notes.common.kafka.model.VoiceNoteUploaded
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.io.InputStream

@Service
class WhisperASRService(
    private val minioClient: MinioClient,
    @Value("\${transcription.endpoint.host}") val transcriptionEndpoint: String
) : ASRService {
    val log = LoggerFactory.getLogger(javaClass)
    override fun downloadAndTranscribeAudioFile(voiceNoteUploaded: VoiceNoteUploaded): String? {

        // Fetch the file from MinIO as InputStream
        val stream: InputStream = minioClient.getObject(
            GetObjectArgs.builder().bucket(voiceNoteUploaded.bucketName).`object`(voiceNoteUploaded.fileName).build()
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

}