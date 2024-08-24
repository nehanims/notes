package notes.config.kafka
/*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Serializer

//TODO: Are these classes necessary? Are there no existing serializers that can be used?
class GenericSerializer<T>(private val serializer: KSerializer<T>) : Serializer<T> {
    override fun serialize(topic: String?, data: T?): ByteArray {
        return Json.encodeToString(serializer, data!!).toByteArray()
    }
}
*/

import com.fasterxml.jackson.databind.ObjectMapper
import notes.voice.domain.VoiceNoteUploaded
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.LoggerFactory


class GenericSerializer<T> : Serializer<T> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: T?): ByteArray? {
        log.info("Serializing...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing message to ByteArray[]")
        )
    }

    override fun close() {}
}