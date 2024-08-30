package notes.transcribe.domain

// VoiceNote.kt
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import java.time.LocalDateTime


@Entity
class Transcription (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val transcriptionFilename: String,
    val voiceNoteId: Long,
    @Column(length = 1024)
    val audioFilename: String,
    val timestamp: LocalDateTime = LocalDateTime.now()//TODO: Change to ZonedDateTime?
)