package notes.voice.database

// VoiceNoteRepository.kt
import org.springframework.data.jpa.repository.JpaRepository

interface VoiceNoteRepository : JpaRepository<VoiceNote, Long>

