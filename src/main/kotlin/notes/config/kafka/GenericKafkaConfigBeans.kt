package notes.config.kafka

import genericKafkaTemplate
import notes.transcribe.domain.VoiceNoteTranscribed
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.KafkaTemplate

@Bean
fun myMessageKafkaTemplate(): KafkaTemplate<String, VoiceNoteTranscribed> {
    return genericKafkaTemplate(VoiceNoteTranscribed.serializer())
}

@Bean
fun myMessageKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, VoiceNoteTranscribed> {
    return genericKafkaListenerContainerFactory(VoiceNoteTranscribed.serializer())
}
