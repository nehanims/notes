package notes.metrics.consumer

import notes.config.websocket.VoiceNoteWebsocketService
import notes.transcribe.domain.VoiceNoteTranscribed
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class NotesConsumer(@Autowired private val wsService: VoiceNoteWebsocketService) {
    private val logger = LoggerFactory.getLogger(javaClass)
    @KafkaListener(
        topics = ["\${kafka.topics.voice-note-transcription}"],
        groupId = "transcribed-consumer",
        containerFactory = "kafkaTranscribedListenerContainerFactory"
    )//TODO change group id name to something more meaningful
    fun listenGroupVoiceNoteTranscribed
                (voiceNoteTranscribedConsumerRecord: ConsumerRecord<Any, VoiceNoteTranscribed>, ack: Acknowledgment) {
        logger.info("Message received key {}", voiceNoteTranscribedConsumerRecord.key())
        logger.info("Message received value {}", voiceNoteTranscribedConsumerRecord.value())
        voiceNoteTranscribedConsumerRecord.value()?.let { wsService.processTranscription(it) }
       //TODO: this should be transactional - dont acknowledge until the message is processed
        ack.acknowledge()
    }
}