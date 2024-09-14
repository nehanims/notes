package notes.metrics.consumer

import notes.common.websocket.VoiceNoteWebsocketService
import notes.metrics.service.MetricsService
import notes.common.kafka.model.VoiceNoteTranscribed
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
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
    fun listenGroupVoiceNoteTranscribed(
        voiceNoteTranscribedConsumerRecord: ConsumerRecord<Any, VoiceNoteTranscribed>,
        ack: Acknowledgment) {
        logger.info("Message received {}", voiceNoteTranscribedConsumerRecord)
        // Send the transcription update via WebSocket
        wsService.processTranscription(voiceNoteTranscribedConsumerRecord.value())
        // extract metrics from the transcribed text
        metricsService.processTranscription(voiceNoteTranscribedConsumerRecord.value())

        ack.acknowledge()
    }


}