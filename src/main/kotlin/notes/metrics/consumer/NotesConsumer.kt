package notes.metrics.consumer

import notes.config.websocket.VoiceNoteWebsocketService
import notes.metrics.service.MetricsService
import notes.kafka.model.VoiceNoteTranscribed
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class NotesConsumer(private val wsService: VoiceNoteWebsocketService,
                    private val metricsService: MetricsService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${kafka.topics.voice-note-transcription}"],
        groupId = "\${kafka.consumer.group-id.transcription}",
        containerFactory = "kafkaTranscribedListenerContainerFactory"
    )
    fun listenGroupVoiceNoteTranscribed
                (voiceNoteTranscribedConsumerRecord: ConsumerRecord<Any, VoiceNoteTranscribed>, ack: Acknowledgment) {
        sendTranscriptionToWebsocketClients(voiceNoteTranscribedConsumerRecord)
        metricsService.processTranscription(voiceNoteTranscribedConsumerRecord.value())
        //TODO: https://github.com/nehanims/notes/issues/38#issuecomment-2316294879
        ack.acknowledge()
    }

    private fun sendTranscriptionToWebsocketClients(voiceNoteTranscribedConsumerRecord: ConsumerRecord<Any, VoiceNoteTranscribed>) {
        logger.info("Message received key {}", voiceNoteTranscribedConsumerRecord.key())
        logger.info("Message received value {}", voiceNoteTranscribedConsumerRecord.value())
        voiceNoteTranscribedConsumerRecord.value()?.let { wsService.processTranscription(it) }
    }
}