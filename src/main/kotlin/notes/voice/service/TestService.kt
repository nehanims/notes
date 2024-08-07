package notes.voice.service
//Service to return the string "Hello World"
import org.springframework.stereotype.Service

@Service
class TestService {
    fun getHelloWorld(): String {
        return "Hello World Changed"
    }
}