package notes.voice.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping("/hello")
    fun getHelloWorld(): String {
        return "Hello World"
    }
}