import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import kotlinx.serialization.KSerializer

inline fun <reified T> genericProducerFactory(serializer: KSerializer<T>): ProducerFactory<String, T> {
    val configProps = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to GenericSerializer(serializer)::class.java
    )
    return DefaultKafkaProducerFactory(configProps)
}

inline fun <reified T> genericKafkaTemplate(serializer: KSerializer<T>): KafkaTemplate<String, T> {
    return KafkaTemplate(genericProducerFactory(serializer))
}
