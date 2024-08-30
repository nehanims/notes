package notes.transcribe.service

import notes.common.kafka.model.VoiceNoteUploaded

interface ASRService {
    fun downloadAndTranscribeAudioFile(voiceNoteUploaded: VoiceNoteUploaded): String?
}