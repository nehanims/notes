package notes.common.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import notes.common.kafka.model.MetricDTO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class VoiceNotesWebSocketHandler : TextWebSocketHandler() {
    private val sessions = mutableListOf<WebSocketSession>()
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)


    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)

    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
    }

    fun sendTranscriptionUpdate(audioFilename: String?, transcribedFilename: String) {

        val message = audioFilename?.let { TranscriptionMessage("transcription", it, transcribedFilename) }
        val jsonMessage = objectMapper.writeValueAsString(message)
        sessions.forEach {
            try {
                if(it.isOpen) it.sendMessage(TextMessage(jsonMessage))
            } catch (e: Exception) {
                log.error("Error sending transcription update", e)
                //sessions.remove(it) TODO: handle this
            }
        }
    }

    fun sendMetricsUpdate(transcriptionFilename: String, metrics: List<MetricDTO>) {
        if (metrics.isEmpty())
            return

        val metricsMessage = MetricsMessage(metrics = metrics, transcriptionFilename = transcriptionFilename, audioFilename=metrics[0].voiceNoteFilename?:"audio_filename_missing")//TODO throw exception if voiceNoteFilename is missing
        val jsonMessage = objectMapper.writeValueAsString(metricsMessage)
        sessions.forEach {
            try {
                if(it.isOpen) it.sendMessage(TextMessage(jsonMessage))
            } catch (e: Exception) {
                log.error("Error sending metrics update", e)
                //sessions.remove(it) TODO: handle this
            }
        }
    }
}