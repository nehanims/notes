package notes.voice.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class VoiceNoteUploaded (
@JsonProperty("name")
val name: String,
@JsonProperty("sku")
val sku: String?
)