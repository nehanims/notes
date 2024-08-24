package notes.config.kafka

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer


class GenericDeserializer<T>(private val deserializer: KSerializer<T>) : Deserializer<T> {
    override fun deserialize(topic: String?, data: ByteArray?): T {
        return Json.decodeFromString(deserializer, data?.toString(Charsets.UTF_8)!!)
    }
}
