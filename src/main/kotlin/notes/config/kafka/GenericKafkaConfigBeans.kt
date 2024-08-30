package notes.config.kafka

import genericKafkaTemplate
import notes.common.kafka.model.MetricDTO
import notes.common.kafka.model.VoiceNoteTranscribed
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.KafkaTemplate

@EnableKafka
@Configuration
class KafkaConfigVoiceNoteTranscribed
{
    @Bean
    fun voiceNotesTranscribedKafkaTemplate(): KafkaTemplate<String, VoiceNoteTranscribed> {
        return genericKafkaTemplate()
    }

    @Bean
    fun metricsKafkaTemplate(): KafkaTemplate<String, MetricDTO> {
        return genericKafkaTemplate()
    }

}
