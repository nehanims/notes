package notes.voice.domain

// VoiceNote.kt
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class VoiceNote(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        val filename: String,
        @Column(length = 1024)
        val url: String,
        val length: Long, // length in milliseconds
        val encoding: String,
        val channels: Int,
        val title: String? = null,
        val timestamp: LocalDateTime = LocalDateTime.now()
)
