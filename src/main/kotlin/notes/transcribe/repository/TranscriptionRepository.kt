package notes.transcribe.repository

import notes.transcribe.domain.Transcription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TranscriptionRepository : JpaRepository<Transcription, Long> {

    fun findByTranscriptionFilename(transcriptionFilename:String): Transcription

    //@Query("select t from Transcription t where t.audioFilename in ?1")
    fun findAllByAudioFilenameIn(audioFilenames: Collection<String>): List<Transcription>
}