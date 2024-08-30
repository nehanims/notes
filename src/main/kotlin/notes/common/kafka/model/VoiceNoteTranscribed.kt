package notes.common.kafka.model

import com.fasterxml.jackson.annotation.JsonProperty

data class VoiceNoteTranscribed (
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("transcribedFilename")
    val transcribedFilename: String,
    @JsonProperty("transcribedText")
    val transcribedText: String,
    @JsonProperty("voiceNoteId")
    val voiceNoteId: Long,
    @JsonProperty("voiceNoteFilename")
    val voiceNoteFilename:String

)