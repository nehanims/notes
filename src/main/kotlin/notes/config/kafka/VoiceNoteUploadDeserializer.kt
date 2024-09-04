package notes.config.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import notes.common.kafka.model.VoiceNoteUploaded
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import kotlin.text.Charsets.UTF_8


class VoiceNoteUploadDeserializer : Deserializer<VoiceNoteUploaded> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): VoiceNoteUploaded? {
        log.info("Deserializing...")
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[]"), UTF_8
            ), VoiceNoteUploaded::class.java
        )
    }

    override fun close() {}

}