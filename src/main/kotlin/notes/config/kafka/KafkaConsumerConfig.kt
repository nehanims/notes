package notes.config.kafka

import notes.common.kafka.model.VoiceNoteTranscribed
import notes.common.kafka.model.VoiceNoteUploaded
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties


@EnableKafka
@Configuration
class KafkaConsumerConfig(
    @Value("\${kafka.bootstrap-address}") private val servers: String,
    @Value("\${kafka.consumer.group-id.voice-note}") private val voiceNoteConsumerGroupId: String,
    @Value("\${kafka.consumer.group-id.transcription}") private val transcriptionConsumerGroupId: String
) {

    @Bean
    fun consumerFactory(): ConsumerFactory<String?, VoiceNoteUploaded?> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        props[ConsumerConfig.GROUP_ID_CONFIG] = voiceNoteConsumerGroupId
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = VoiceNoteUploadDeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, VoiceNoteUploaded> {//TODO how can a producer bean potentially return null???? its whole point is to produce beans!!
        val factory = ConcurrentKafkaListenerContainerFactory<String, VoiceNoteUploaded>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.containerProperties.isSyncCommits = true
        return factory
    }

    @Bean
    fun transcribedConsumerFactory(): ConsumerFactory<String?, VoiceNoteTranscribed?> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        props[ConsumerConfig.GROUP_ID_CONFIG] = transcriptionConsumerGroupId
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = VoiceNoteTranscribedDeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaTranscribedListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, VoiceNoteTranscribed> {//TODO how can a producer bean potentially return null???? its whole point is to produce beans!!
        val factory = ConcurrentKafkaListenerContainerFactory<String, VoiceNoteTranscribed>()
        factory.consumerFactory = transcribedConsumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.containerProperties.isSyncCommits = true
        return factory
    }



}