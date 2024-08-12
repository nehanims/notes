package notes.voice.repository

import notes.voice.domain.VoiceNote
import org.springframework.data.jpa.repository.JpaRepository

interface VoiceNoteRepository : JpaRepository<VoiceNote, Long>