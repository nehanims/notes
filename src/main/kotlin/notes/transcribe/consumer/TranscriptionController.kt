package notes.transcribe.consumer

import notes.transcribe.service.TranscriptionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/transcriptions")
class TranscriptionController(@Autowired private val transcriptionService: TranscriptionService) {

    @GetMapping("{filename}")
    fun getTranscription(@PathVariable filename: String):String {
        return transcriptionService.getTranscription(filename)
    }

}