package notes.config.websocket


data class TranscriptionMessage(val type: String = "transcription", val fileName: String, val transcription: String)
