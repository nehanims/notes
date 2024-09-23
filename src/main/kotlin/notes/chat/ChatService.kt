package notes.chat

import notes.common.s3.service.MinIOService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ChatService(private val minIOService: MinIOService,
                  @Value("\${minio.bucket-name}") private val bucket: String) {
    fun saveChat(chatMessagesJson: String) : String {
        val fileName = "chat-messages-${System.currentTimeMillis()}.json"
        minIOService.uploadTextString(bucket, fileName, chatMessagesJson)
        return fileName
    }

    fun getChat(filename: String): String {
        return minIOService.getTextFileStream(filename, bucket)
    }

}
