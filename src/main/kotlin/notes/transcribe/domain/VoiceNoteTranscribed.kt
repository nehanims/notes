package notes.transcribe.domain
import kotlinx.serialization.Serializable

@Serializable
data class VoiceNoteTranscribed(val id: String, val text: String, val voiceNoteId: String)
