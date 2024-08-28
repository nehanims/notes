package notes.ollama.client

import notes.ollama.model.OllamaGenerateRequestBody
import notes.ollama.model.OllamaGenerateResponseBody
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@HttpExchange("/api")
public interface OllamaClient {
    @PostExchange("/generate")
    fun generate(@RequestBody generateRequest: OllamaGenerateRequestBody) : OllamaGenerateResponseBody
}