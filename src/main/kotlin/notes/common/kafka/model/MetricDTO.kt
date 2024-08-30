package notes.common.kafka.model

import com.fasterxml.jackson.annotation.JsonProperty
import notes.metrics.domain.MetricApprovalStatus
import notes.metrics.domain.MetricSource

data class MetricDTO (
    @JsonProperty("id")
    val id: Long?,
    @JsonProperty("type")
    val type: String,//TODO make it an enum or from DB
    @JsonProperty("summary")
    val summary: String,
    @JsonProperty("approvalStatus")
    val approvalStatus: MetricApprovalStatus?,
    @JsonProperty("source")
    val source: MetricSource?,
    @JsonProperty("llm")
    val llm: String?,
    @JsonProperty("transcribedFileId")
    val transcribedFileId: Long,
    @JsonProperty("transcribedFilename")
    val transcribedFilename: String?,
    @JsonProperty("voiceNoteId")
    val voiceNoteId: Long,
    @JsonProperty("voiceNoteFilename")
    val voiceNoteFilename: String?

)