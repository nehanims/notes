package notes.common.kafka.model

import com.fasterxml.jackson.annotation.JsonProperty

data class VoiceNoteUploaded (
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("fileName")
    val fileName: String,
    @JsonProperty("bucketName")
    val bucketName: String,
//    @JsonProperty
//    val created: ZonedDateTime//TODO handle the serialization of ZonedDateTime


)