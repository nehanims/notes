package notes.voice.service

import io.minio.MinioClient
import io.minio.GetObjectArgs

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.MultiValueMap
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity

import java.io.InputStream

@Service
class TranscriptionService(
    @Autowired private val minioClient: MinioClient,
    @Value("\${minio.bucket-name}") private val minioBucketName: String
) {

    /*private val minioClient: MinioClient = MinioClient.builder()
        .endpoint(minioUrl)
        .credentials(minioAccessKey, minioSecretKey)
        .build()*/

    /*
    fun downloadAndSendFileToEndpoint(fileName: String) {
        // Download the file from MinIO
        val inputStream: InputStream = minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucketName)
                .`object`(fileName)
                .build()
        )

        // Prepare the multipart request
        val headers = HttpHeaders().apply {
            contentType = MediaType.MULTIPART_FORM_DATA
            set("accept", "application/json")
        }

        val bodyBuilder = MultipartBodyBuilder()
        bodyBuilder.part("audio_file", inputStream, MediaType.APPLICATION_OCTET_STREAM)
            .filename(fileName)
            .header("Content-Type", "video/webm") // Ensure the correct content type

        val httpEntity: HttpEntity<MultiValueMap<String, HttpEntity<*>>> = HttpEntity(bodyBuilder.build(), headers)

        val restTemplate = RestTemplate()
        val response = restTemplate.postForEntity(
            "http://localhost:9000/asr?encode=true&task=transcribe&language=en&word_timestamps=false&output=txt",
            httpEntity,
            String::class.java
        )

        // Handle the response
        println("Response from server: ${response.body}")
    }
    */

    private val log = LoggerFactory.getLogger(javaClass)

    fun downloadAndSendFileToEndpoint(fileName: String) : ResponseEntity<String> {


        // Fetch the file from MinIO as InputStream
        val stream: InputStream = minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(minioBucketName)
                .`object`(fileName)
                .build()
        )

        // Convert InputStream to ByteArray
        val byteArray = stream.use { it.readBytes() }

        // Prepare the ByteArrayResource for the file
        val fileAsResource = object : ByteArrayResource(byteArray) {
            override fun getFilename(): String {
                return fileName // Specify the filename
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
        val restTemplate = RestTemplate()
        val response = restTemplate.postForEntity(
            "http://localhost:9900/asr?encode=true&task=transcribe&language=en&word_timestamps=false&output=txt",
            requestEntity,
            String::class.java
        )

        // Handle the response
        log.info("Response: ${response.body}")
        return response
    }

    fun transcribe(fileName: String) : ResponseEntity<String> {
        return downloadAndSendFileToEndpoint(fileName)
    }
}



