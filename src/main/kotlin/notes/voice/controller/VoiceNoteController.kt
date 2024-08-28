package notes.voice.controller

import notes.voice.domain.VoiceNote
import notes.voice.service.VoiceNoteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/voice-notes")
class VoiceNoteController {

    @Autowired
    private lateinit var voiceNoteService: VoiceNoteService

    @PostMapping("/upload")
    @ResponseBody
    fun uploadAudioFile(
        @RequestParam("audio") file: MultipartFile,
        @RequestParam("length") length: String,
        @RequestParam("encoding") encoding: String,
        @RequestParam("channels") channels: String,
        @RequestParam("title", required = false) title: String
    ): VoiceNote {
        val metadata = mapOf(
            "length" to length,
            "encoding" to encoding,
            "channels" to channels,
            "title" to title
        )
        return voiceNoteService.uploadFile(file, metadata)
    }



    @GetMapping("/today")
    fun getTodaysRecordings(): List<Pair<String,String>> {
        return voiceNoteService.getTodaysRecordings()
    }

    @GetMapping("/audio/{filename}")
    fun getAudioFile(@PathVariable filename: String): ByteArray {
        return voiceNoteService.getAudioFile(filename)
    }

}
