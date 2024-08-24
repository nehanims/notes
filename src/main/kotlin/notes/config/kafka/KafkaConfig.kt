package notes.config.kafka


import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaConfig(
    @Value("\${kafka.bootstrap-address}")
    private val servers: String,
    @Value("\${kafka.topics.voice-note}")
    private val voiceNoteUploadedTopic: String,
    @Value("\${kafka.topics.voice-note-transcription}")
    private val voiceNoteTranscribedTopic: String
) {

    //TODO: Is this needed in spring boot?
    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        return KafkaAdmin(configs)
    }

    //TODO: Are these doing anything
    @Bean("voiceNoteTopic")
    fun voiceNoteUploadedTopicFactory(): NewTopic {
        return NewTopic(voiceNoteUploadedTopic, 1, 1.toShort())
    }

    //TODO: Are these doing anything
    @Bean("transcribedTopic")
    fun voiceNoteTranscribedTopicFactory(): NewTopic {
        return NewTopic(voiceNoteTranscribedTopic, 1, 1.toShort())
    }

}