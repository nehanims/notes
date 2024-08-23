package notes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
//TODO : Go through the Spring Kotlin docs to see what changes need to be made for things to work with kotlin https://spring.io/guides/tutorials/spring-boot-kotlin
@SpringBootApplication
class NotesApplication

fun main(args: Array<String>) {
	runApplication<NotesApplication>(*args)
}
