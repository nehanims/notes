import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import kotlinx.serialization.KSerializer
import notes.config.kafka.GenericSerializer

inline fun <reified T> genericProducerFactory(): ProducerFactory<String, T> {
    val configProps = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to GenericSerializer::class.java
    )
    return DefaultKafkaProducerFactory(configProps)
}

inline fun <reified T> genericKafkaTemplate(): KafkaTemplate<String, T> {
    return KafkaTemplate(genericProducerFactory())
}
