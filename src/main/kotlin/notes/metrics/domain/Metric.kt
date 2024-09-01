package notes.metrics.domain

import jakarta.persistence.*

@Entity
class Metric (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val type: String,//TODO make it an enum or from DB
    val summary: String,
    @Enumerated(EnumType.STRING)
    val approvalStatus: MetricApprovalStatus,

    val source: MetricSource?,
    val llm: String?,
    val transcribedFileId: Long,//TODO FK constraint? Or no?
    val transcribedFilename: String?,
    val voiceNoteId: Long,
    val voiceNoteFilename: String?
){
    fun withApprovalStatus(status: MetricApprovalStatus): Metric {
        return Metric(
            id = this.id,
            type = this.type,
            summary = this.summary,
            approvalStatus = status,
            source = this.source,
            llm = this.llm,
            transcribedFileId = this.transcribedFileId,
            transcribedFilename = this.transcribedFilename,
            voiceNoteId = this.voiceNoteId,
            voiceNoteFilename = this.voiceNoteFilename
        )
    }
}
