package notes.transcribe.consumer

import notes.voice.domain.VoiceNoteUploaded
import notes.transcribe.service.TranscriptionService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

//TODO should this be annotated as a service? or just a component? It wont really change anything functionally because i think they are combinations of the same set of annotations
@Component
class VoiceNoteUploadConsumer(@Autowired
               private val transcriptionService: TranscriptionService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    //TODO maybe call this VoiceNoteUploadedEventConsumer
    @KafkaListener(topics = ["\${kafka.topics.voice-note}"], groupId = "ppr")
    fun listenGroupVoiceNoteUpload(voiceNoteUploadRecord: ConsumerRecord<Any, VoiceNoteUploaded>, ack: Acknowledgment) {
        logger.info("Message received key {}", voiceNoteUploadRecord.key())
        logger.info("Message received value {}", voiceNoteUploadRecord.value().sku)

        if (voiceNoteUploadRecord.value().sku == null) {
            logger.error("Message received with null sku")
            ack.acknowledge()
            return
        } else if (voiceNoteUploadRecord.value().sku!!.contains("http")) {
            logger.info("not processing message with http sku")
            ack.acknowledge()
            return
        }

        val transcribed = transcriptionService.transcribe(voiceNoteUploadRecord.value().sku!!)
        logger.info("Transcribed: ${transcribed.body}")
        //TODO: this should be transactional - dont acknowledge until the message is processed
        ack.acknowledge()
    }
}