package notes.config.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import notes.kafka.model.VoiceNoteUploaded
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.LoggerFactory


class VoiceNoteUploadSerializer : Serializer<VoiceNoteUploaded> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: VoiceNoteUploaded?): ByteArray? {
        log.info("Serializing...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing TestMessage to ByteArray[]")
        )
    }

    override fun close() {}
}