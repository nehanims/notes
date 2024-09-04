package notes.config.kafka

//
//import com.fasterxml.jackson.databind.ObjectMapper
//import org.apache.kafka.common.errors.SerializationException
//import org.apache.kafka.common.serialization.Deserializer
//import org.slf4j.LoggerFactory
//import java.nio.charset.StandardCharsets.UTF_8
//
//class GenericDeserializer<T>(private val clazz: Class<T>) : Deserializer<T> {
//    private val objectMapper = ObjectMapper()
//    private val log = LoggerFactory.getLogger(javaClass)
//
//    override fun deserialize(topic: String?, data: ByteArray?): T? {
//        log.info("Deserializing...")
//        return objectMapper.readValue(
//            String(data ?: throw SerializationException("Error when deserializing byte[]"), UTF_8),
//            clazz
//        )
//    }
//
//    override fun close() {}
//}
