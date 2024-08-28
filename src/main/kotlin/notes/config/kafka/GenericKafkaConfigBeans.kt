package notes.config.kafka

import genericKafkaTemplate
import notes.metrics.domain.Metric
import notes.transcribe.domain.VoiceNoteTranscribed
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
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
    fun metricsKafkaTemplate(): KafkaTemplate<String, Metric> {
        return genericKafkaTemplate()
    }

}
