package notes.config

import notes.common.pubtator3.client.PubTator3Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
@Configuration
class PubTator3Config {
    @Bean
    fun pubtator3Client(@Value("\${pubtator3.host}") pubtator3Url:String, webProperties: WebProperties): PubTator3Client{
        val webClient = WebClient.builder()
            //.baseUrl(pubtator3Url)
            .baseUrl("https://www.ncbi.nlm.nih.gov/")//TODO: remove hardcoded url see why value injection is not working.. why is it extracting just the host IP from the url? is it because of name being ollama.host?!?
            .codecs { configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) }
            .build()
        val httpServiceProxyFactory =
            HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient))
                .build()
        return httpServiceProxyFactory.createClient(PubTator3Client::class.java)
    }
}



