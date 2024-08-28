package notes.config


import notes.ollama.client.OllamaClient
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
    fun ollamaClient(@Value("\${ollama.host}") ollamaUrl:String, webProperties: WebProperties): OllamaClient {
        val webClient = WebClient.builder()
            //.baseUrl(ollamaUrl)
            .baseUrl("http://192.168.50.35:11434")//TODO: remove hardcoded url see why value injection is not working.. why is it extracting just the host IP from the url? is it because of name being ollama.host?!?
            .build()
        val httpServiceProxyFactory =
            HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient))
                .build()
        return httpServiceProxyFactory.createClient(OllamaClient::class.java)
    }


}