package notes.voice.domain

// VoiceNote.kt
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Entity
class VoiceNote(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        val fileName: String,
        val bucketName: String,
        val title: String? = null,
        val created: ZonedDateTime = ZonedDateTime.now()
)
