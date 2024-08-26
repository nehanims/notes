package notes.config.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class TranscriptionWebSocketHandler : TextWebSocketHandler() {
    private val sessions = mutableListOf<WebSocketSession>()
    private val objectMapper = ObjectMapper()


    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)

    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
    }

    fun sendTranscriptionUpdate(fileName: String?, transcription: String) {
        val message = fileName?.let { TranscriptionMessage("transcription", it, transcription) }
        val jsonMessage = objectMapper.writeValueAsString(message)
        sessions.forEach { it.sendMessage(TextMessage(jsonMessage)) }
    }
}