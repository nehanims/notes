package notes.voice.controller

import notes.voice.service.VoiceNoteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/upload")
class VoiceNoteController {

    @Autowired
    private lateinit var voiceNoteService: VoiceNoteService

    @PostMapping("/audio")
    fun uploadAudioFile(
        @RequestParam("audio") file: MultipartFile,
        @RequestParam("length") length: String,
        @RequestParam("encoding") encoding: String,
        @RequestParam("channels") channels: String,
        @RequestParam("title", required = false) title: String
    ): String {
        val metadata = mapOf(
            "length" to length,
            "encoding" to encoding,
            "channels" to channels,
            "title" to title
        )
        return voiceNoteService.uploadFile(file, metadata)
    }
}
