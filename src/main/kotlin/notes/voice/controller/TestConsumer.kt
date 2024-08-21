package notes.voice.controller

import notes.voice.domain.TestMessage
import notes.voice.service.TranscriptionService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

//TODO should this be a service? or just a component?
@Component
class Consumer(@Autowired
               private val transcriptionService: TranscriptionService) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["\${kafka.topics.test_topic}"], groupId = "ppr")
    fun listenGroupFoo(consumerRecord: ConsumerRecord<Any, TestMessage>, ack: Acknowledgment) {
        logger.info("Message received key {}", consumerRecord.key())
        logger.info("Message received value {}", consumerRecord.value().sku)

        if (consumerRecord.value().sku == null) {
            logger.error("Message received with null sku")
            ack.acknowledge()
            return
        } else if (consumerRecord.value().sku!!.contains("http")) {
            logger.info("not processing message with http sku")
            ack.acknowledge()
            return
        }

        val transcribed = transcriptionService.transcribe(consumerRecord.value().sku!!)
        logger.info("Transcribed: ${transcribed.body}")
        //TODO: this should be transactional - dont acknowledge until the message is processed
        ack.acknowledge()
    }
}