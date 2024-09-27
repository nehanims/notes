package notes.config


import notes.common.ollama.client.OllamaClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory


@Configuration
class OllamaClientConfig {

    @Bean
    fun ollamaClient(@Value("\${ollama.url}") ollamaUrl:String, webProperties: WebProperties): notes.common.ollama.client.OllamaClient {
        val webClient = WebClient.builder()
            .baseUrl(ollamaUrl)
            .build()
        val httpServiceProxyFactory =
            HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient))
                .build()
        return httpServiceProxyFactory.createClient(notes.common.ollama.client.OllamaClient::class.java)
    }


}