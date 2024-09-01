package notes.metrics.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import notes.common.websocket.VoiceNotesWebSocketHandler
import notes.common.kafka.model.MetricDTO
import notes.metrics.domain.MetricSource

import notes.common.kafka.model.VoiceNoteTranscribed
import notes.common.ollama.model.OllamaGenerateRequestBody
import notes.metrics.domain.Metric
import notes.metrics.domain.MetricApprovalStatus
import notes.metrics.repository.MetricRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import java.util.*

@Service

class MetricsService(
    private val generatedMetricRepository: MetricRepository,
    private val ollamaClient: notes.common.ollama.client.OllamaClient,
    private val ollamaPromptService: notes.common.ollama.client.OllamaPromptService,
    private val voiceNotesWebSocketHandler: VoiceNotesWebSocketHandler,
    private val kafkaTemplate: KafkaTemplate<String, MetricDTO>,
    @Value("\${kafka.topics.generated-metrics}") private val generatedMetricsTopic: String,

    ) {
    val log = LoggerFactory.getLogger(javaClass)
    fun processTranscription(voiceNoteTanscribed: VoiceNoteTranscribed) {
        // send the transcription to rewrite service
        val original = voiceNoteTanscribed.transcribedText
        val rewritten = rewriteTranscription(original)
        // send the rewritten and original transcription to metrics service(experimental feature for comparison)
        log.info("Original transcription: $original")
        log.info("Rewritten transcription: $rewritten")

        // extract metrics from the transcribed text
        val parsedMetrics = getMetrics(original, MetricSource.ORIGINAL_TRANSCRIPT,
            voiceNoteTanscribed,
            rewritten)
        log.info("Parsed Metrics: $parsedMetrics")

        // save the metrics to the database
        val generatedMetrics = parsedMetrics.map{
            Metric(
                type=it.type,
                summary=it.summary,
                approvalStatus=MetricApprovalStatus.PENDING,
                llm=ollamaPromptService.getModelForMetricExtraction(),
                source = MetricSource.ORIGINAL_TRANSCRIPT,
                transcribedFileId = voiceNoteTanscribed.id,
                transcribedFilename = voiceNoteTanscribed.transcribedFilename,
                voiceNoteId = voiceNoteTanscribed.voiceNoteId,
                voiceNoteFilename = voiceNoteTanscribed.voiceNoteFilename
            )
        }
        val savedMetrics=generatedMetricRepository.saveAll(generatedMetrics)
        log.info("Saved Generated Metrics: $savedMetrics")

        // publish the metrics to kafka
        val metrics = savedMetrics.stream()
            .map {
                MetricDTO(
                    id = it.id,//Generate UUID
                    type = it.type,
                    summary = it.summary,
                    approvalStatus = it.approvalStatus,
                    llm = it.llm,
                    source = it.source,
                    transcribedFileId = it.transcribedFileId,
                    transcribedFilename = it.transcribedFilename,
                    voiceNoteId = it.voiceNoteId,
                    voiceNoteFilename = it.voiceNoteFilename
                )
            }.toList()
        publishMetricsToKafka(metrics)

        // send the metrics to the websocket
        sendToWebsocket(voiceNoteTanscribed.transcribedFilename, metrics)

        //TODO: publish metrics for rewritten text also
        val rewrittenMetrics = getMetrics(rewritten, MetricSource.REWRITTEN_TRANSCRIPT,
            voiceNoteTanscribed,
            "N/A - this is the rewritten text")
        log.info("Rewritten Metrics: $rewrittenMetrics")

    }

    //TODO https://learn.deeplearning.ai/courses/chatgpt-prompt-eng/lesson/1/introduction
    private fun rewriteTranscription(textToRewrite: String): String {
        return ollamaClient.generate(
            OllamaGenerateRequestBody(
                model = ollamaPromptService.getModelForRewrite(),//TODO: make the model configurable
                prompt = ollamaPromptService.getRewritePrompt(textToRewrite),
                stream = false
            )
        ).response
    }

    private fun getMetrics(text:String,
                           source: MetricSource,
                           voiceNoteTanscribed: VoiceNoteTranscribed,
                           transcribedTextRewrite:String): List<MetricDTO> {
        val ollamaMetricsTextResponse = getMetricsTextFromOllama(text)
        log.info("Metrics text from Ollama: $ollamaMetricsTextResponse")
        return parseMetrics(ollamaMetricsTextResponse, source, voiceNoteTanscribed, transcribedTextRewrite)
    }

    private fun getMetricsTextFromOllama(transcribedText: String): String {

        val prompt = ollamaPromptService.getMetricPrompt(transcribedText)
        return ollamaClient.generate(
            OllamaGenerateRequestBody(
                model = ollamaPromptService.getModelForMetricExtraction(),
                prompt = prompt,
                stream = false
            )
        ).response
    }

    fun parseMetrics(jsonString: String,
                     source: MetricSource,
                     voiceNoteTanscribed: VoiceNoteTranscribed,
                     transcribedTextRewrite:String): List<MetricDTO> {
        return try {
            val cleanedJson = jsonString.trim()
                .removePrefix("```json")
                .removeSuffix("```")
                .trim()

            val objectMapper = jacksonObjectMapper()
            return objectMapper.readValue(cleanedJson, Array<MetricDTO>::class.java)
                .toList()

        } catch (e: Exception) {
            println("Error parsing JSON: ${e.message}")
            return emptyList()
        }
    }

    private fun publishMetricsToKafka(metrics: List<MetricDTO>) {

        metrics.forEach() {
            log.info("Sending metric to Kafka {}", it)
            val message: Message<MetricDTO> = MessageBuilder
                .withPayload(it)
                .setHeader(KafkaHeaders.TOPIC, generatedMetricsTopic)
                //.setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplate.send(message)
            log.info("Message sent with success")
        }
    }

    private fun sendToWebsocket(transcriptionFilename:String, metrics: List<MetricDTO>) {
        voiceNotesWebSocketHandler.sendMetricsUpdate(transcriptionFilename, metrics)
    }

}