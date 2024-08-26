package notes.config.websocket

import notes.transcribe.domain.VoiceNoteTranscribed
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


// In your VoiceNoteService or similar
@Service
class VoiceNoteWebsocketService(
    @Autowired
    private val transcriptionWebSocketHandler: TranscriptionWebSocketHandler
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun processTranscription(voiceNoteTranscribed: VoiceNoteTranscribed) {
        val fileName = voiceNoteTranscribed.voiceNoteFilename

        logger.info("***** Processing transcription for $fileName ")
        // Simulate transcription process
        // In a real scenario, this would be an asynchronous process
        val transcription = voiceNoteTranscribed.transcribedText

        // Send the transcription update via WebSocket

        transcriptionWebSocketHandler.sendTranscriptionUpdate(fileName, transcription)

    }
}