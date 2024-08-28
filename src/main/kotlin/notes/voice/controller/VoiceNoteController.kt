package notes.voice.controller

import notes.voice.domain.VoiceNote
import notes.voice.service.VoiceNoteService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/voice-notes")
class VoiceNoteController(private var voiceNoteService: VoiceNoteService) {

    @PostMapping("/upload")
    @ResponseBody
    fun uploadAudioFile(
        @RequestParam("audio") file: MultipartFile
    ): VoiceNote {
        return voiceNoteService.processVoiceNote(file)
    }

    @GetMapping("/today")
    fun getTodayRecordings(): List<Pair<String,String>> {
        return voiceNoteService.getPast24HoursRecordings()
    }

    @GetMapping("/audio/{filename}")
    fun getAudioFile(@PathVariable filename: String): ByteArray {
        return voiceNoteService.getAudioFile(filename)
    }

}
