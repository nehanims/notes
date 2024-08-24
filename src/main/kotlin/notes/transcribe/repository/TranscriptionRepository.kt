package notes.transcribe.repository

import notes.transcribe.domain.Transcription
import org.springframework.data.jpa.repository.JpaRepository

interface TranscriptionRepository : JpaRepository<Transcription, Long>