import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Serializer

//TODO: Are these classes necessary? Are there no existing serializers that can be used?
class GenericSerializer<T>(private val serializer: KSerializer<T>) : Serializer<T> {
    override fun serialize(topic: String?, data: T?): ByteArray {
        return Json.encodeToString(serializer, data!!).toByteArray()
    }
}
