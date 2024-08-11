package notes.voice.service

import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.http.Method
import notes.voice.database.VoiceNote
import notes.voice.database.VoiceNoteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.util.concurrent.TimeUnit

@Service
class VoiceNoteService {

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

        return fileUrl
    }
}
