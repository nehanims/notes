package notes.voice.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class TestMessage (
@JsonProperty("name")
val name: String,
@JsonProperty("sku")
val sku: String?
)