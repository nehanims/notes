package notes.voice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
//TODO : Go through the Spring Kotlin docs to see what changes need to be made for things to work with kotlin https://spring.io/guides/tutorials/spring-boot-kotlin
@SpringBootApplication
class VoiceApplication

fun main(args: Array<String>) {
	runApplication<VoiceApplication>(*args)
}
