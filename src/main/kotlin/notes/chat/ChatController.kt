package notes.chat

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/chat")
class ChatController(val chatService: ChatService) {

    @PostMapping("/save")
    fun saveChat(@RequestBody chatMessagesJson: String): String {
        return chatService.saveChat(chatMessagesJson)
    }

    @GetMapping("/get/{filename}")
    fun getChat(@PathVariable filename:String): String {
        return chatService.getChat(filename)
    }
}


