package notes.kafka.model

import com.fasterxml.jackson.annotation.JsonProperty

data class VoiceNoteTranscribed (
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("transcribedFilename")
    val transcribedFilename: String,
    @JsonProperty("transcribedText")
    val transcribedText: String,
    @JsonProperty("voiceNoteUrl")
    val voiceNoteFilename: String?

)