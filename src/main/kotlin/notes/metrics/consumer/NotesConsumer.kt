package notes.metrics.consumer

import notes.config.websocket.VoiceNoteWebsocketService
import notes.metrics.service.MetricsService
import notes.transcribe.domain.VoiceNoteTranscribed
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class NotesConsumer(@Autowired private val wsService: VoiceNoteWebsocketService,
                    @Autowired private val metricsService: MetricsService
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    @KafkaListener(
        topics = ["\${kafka.topics.voice-note-transcription}"],
        groupId = "transcribed-consumer",
        containerFactory = "kafkaTranscribedListenerContainerFactory"
    )//TODO change group id name to something more meaningful
    fun listenGroupVoiceNoteTranscribed
                (voiceNoteTranscribedConsumerRecord: ConsumerRecord<Any, VoiceNoteTranscribed>, ack: Acknowledgment) {
        sendTranscriptionToWebsocketClients(voiceNoteTranscribedConsumerRecord)
        metricsService.processTranscription(voiceNoteTranscribedConsumerRecord.value())

        //TODO: this should be transactional - dont acknowledge until the message is processed - kafka consumer kinda does this kind of transaction management for you because it will not acknowledge the message until it is fully processed with no failure BUT the database and datastore data will get persisted so think about whether you want to acknowledge the message before or after the data is persisted
        ack.acknowledge()
    }

    private fun sendTranscriptionToWebsocketClients(voiceNoteTranscribedConsumerRecord: ConsumerRecord<Any, VoiceNoteTranscribed>) {
        logger.info("Message received key {}", voiceNoteTranscribedConsumerRecord.key())
        logger.info("Message received value {}", voiceNoteTranscribedConsumerRecord.value())
        voiceNoteTranscribedConsumerRecord.value()?.let { wsService.processTranscription(it) }
    }
}