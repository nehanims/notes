package notes.transcribe.domain

// VoiceNote.kt
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime


@Entity
class Transcription (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val filename: String,
    @Column(length = 1024)
    val audioURL: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)