package notes.transcribe.controller

import notes.transcribe.service.TranscriptionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/transcriptions")
class TranscriptionController(@Autowired private val transcriptionService: TranscriptionService) {

    @GetMapping("{filename}")
    fun getTranscription(@PathVariable filename: String):String {
        return transcriptionService.getTranscription(filename)
    }

    @PostMapping("/save/{filename}")
    fun updateTranscription(@PathVariable filename: String, @RequestBody transcription: String): String {
        return transcriptionService.updateTranscription(filename, transcription)
    }

}