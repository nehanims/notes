package notes.voice.config.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import notes.voice.domain.TestMessage
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import kotlin.text.Charsets.UTF_8


class TestDeserializer : Deserializer<TestMessage> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): TestMessage? {
        log.info("Deserializing...")
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to Product"), UTF_8
            ), TestMessage::class.java
        )
    }

    override fun close() {}

}