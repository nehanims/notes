package notes.voice.controller

import notes.voice.service.TestService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(val testService: TestService) {

    @GetMapping("/hello")
    fun getHelloWorld(): String {
        return testService.getHelloWorld()
    }
}