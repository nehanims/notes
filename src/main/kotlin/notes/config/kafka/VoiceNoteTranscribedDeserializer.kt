package notes.config.kafka


import com.fasterxml.jackson.databind.ObjectMapper
import notes.kafka.model.VoiceNoteTranscribed

import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import kotlin.text.Charsets.UTF_8


class VoiceNoteTranscribedDeserializer : Deserializer<VoiceNoteTranscribed> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): VoiceNoteTranscribed? {
        log.info("Deserializing...")
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[]"), UTF_8
            ), VoiceNoteTranscribed::class.java
        )
    }

    override fun close() {}

}