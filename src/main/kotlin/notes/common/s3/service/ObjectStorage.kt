package notes.common.s3.service

import io.minio.GetObjectArgs
import java.io.InputStream

interface ObjectStorageService {
    fun uploadTextString(bucketName: String, objectName: String, content: String)

    fun getTextFileStream(filename: String, bucketName: String): String
}