package notes.voice.config.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import notes.voice.domain.TestMessage
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.LoggerFactory


class TestSerializer : Serializer<TestMessage> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: TestMessage?): ByteArray? {
        log.info("Serializing...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing TestMessage to ByteArray[]")
        )
    }

    override fun close() {}
}