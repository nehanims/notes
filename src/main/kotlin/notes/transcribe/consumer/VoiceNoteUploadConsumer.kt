package notes.transcribe.consumer

import notes.kafka.model.VoiceNoteUploaded
import notes.transcribe.service.TranscriptionService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment

import org.springframework.stereotype.Service


@Service
class VoiceNoteUploadConsumer(private val transcriptionService: TranscriptionService) {
    private val logger = LoggerFactory.getLogger(javaClass)

    //TODO https://github.com/nehanims/notes/issues/38#issue-2492961136
    @KafkaListener(topics = ["\${kafka.topics.voice-note}"], groupId = "\${kafka.consumer.group-id.voice-note}")
    fun listenGroupVoiceNoteUpload(voiceNoteUploadRecord: ConsumerRecord<Any, VoiceNoteUploaded>, ack: Acknowledgment) {
        logger.info("Message received {}", voiceNoteUploadRecord)

        val voiceNoteUploaded = voiceNoteUploadRecord.value()

        val transcribed = transcriptionService.transcribe(voiceNoteUploaded)
        logger.info("Transcribed: ${transcribed.body}")
        //TODO: this should be transactional - dont acknowledge until the message is processed
        ack.acknowledge()
    }
}