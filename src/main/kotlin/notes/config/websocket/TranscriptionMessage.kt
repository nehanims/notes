package notes.config.websocket


data class TranscriptionMessage(val type: String = "transcription",
                                val audioFilename: String,
                                val transcriptionFilename: String)
