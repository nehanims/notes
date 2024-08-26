package notes.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@Configuration
class WebConfig {
    @Bean
    fun corsFilter(@Value("\${cors.allowed-origin}") allowedOrigin: String): CorsFilter {
        val config = CorsConfiguration()
        config.addAllowedOrigin(allowedOrigin) // Allow requests from this origin
        config.addAllowedHeader("*") // Allow all headers
        config.addAllowedMethod("*") // Allow all methods (GET, POST, PUT, DELETE, etc.)
        config.allowCredentials = true // Allow credentials (cookies, etc.)

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)

        return CorsFilter(source)
    }



}