package notes.voice

import notes.ollama.client.OllamaClient
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
@SpringBootTest
class NotesApplicationTests {

	class Person(val name: String, val age: Int)
	@Test
	fun contextLoads() {

		val people = List.of(
				Person("Alice", 25),
				Person("Bob", 30)
		)

		val objectMapper = ObjectMapper()
		val json = objectMapper.writeValueAsString(people);

		System.out.println(json);


	}

}
