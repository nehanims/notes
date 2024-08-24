package notes.config.kafka


import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import kotlinx.serialization.KSerializer

inline fun <reified T> genericConsumerFactory(deserializer: KSerializer<T>): ConsumerFactory<String, T> {
    val configProps = mapOf(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to GenericDeserializer(deserializer)::class.java,
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
        ConsumerConfig.GROUP_ID_CONFIG to "transcribed-consumer"
    )
    return DefaultKafkaConsumerFactory(configProps)
}

inline fun <reified T> genericKafkaListenerContainerFactory(deserializer: KSerializer<T>): ConcurrentKafkaListenerContainerFactory<String, T> {
    val factory = ConcurrentKafkaListenerContainerFactory<String, T>()
    factory.consumerFactory = genericConsumerFactory<T>(deserializer)
    return factory
}
