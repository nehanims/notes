package notes.config.websocket

import notes.transcribe.domain.VoiceNoteTranscribed
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


// In your VoiceNoteService or similar
@Service
class VoiceNoteWebsocketService(
    @Autowired
    private val voiceNotesWebSocketHandler: VoiceNotesWebSocketHandler
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun processTranscription(voiceNoteTranscribed: VoiceNoteTranscribed) {
        val audioFilename = voiceNoteTranscribed.voiceNoteFilename
        val transcribedFilename = voiceNoteTranscribed.transcribedFilename

        // Send the transcription update via WebSocket

        voiceNotesWebSocketHandler.sendTranscriptionUpdate(audioFilename, transcribedFilename)

    }
}