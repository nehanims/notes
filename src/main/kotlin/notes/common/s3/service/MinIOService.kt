package notes.common.s3.service

import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.InputStream

@Service
class MinIOService(private val minioClient: MinioClient,) : ObjectStorageService {

    private val log = LoggerFactory.getLogger(javaClass)
    override
    fun uploadTextString(bucketName: String, objectName: String, content: String) {

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

    override fun getTextFileStream(filename: String, bucketName: String): String {
        val stream: InputStream = minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucketName)
                .`object`(filename)
                .build()
        )
        return stream.bufferedReader().use { it.readText() }
    }
}