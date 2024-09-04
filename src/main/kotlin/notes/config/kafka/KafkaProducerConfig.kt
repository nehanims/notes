package notes.config.kafka

import notes.common.kafka.model.VoiceNoteUploaded
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory


/*
* this makes it configurable programmatically, but you can also add the following
* to application.yml (or application.properties):
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer

* */

@Configuration
class KafkaProducerConfig(
    @Value("\${kafka.bootstrap-address}")
    private val servers: String
) {
    @Bean
    fun producerFactory(): ProducerFactory<String, VoiceNoteUploaded> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = VoiceNoteUploadSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, VoiceNoteUploaded> {
        return KafkaTemplate(producerFactory())
    }
}