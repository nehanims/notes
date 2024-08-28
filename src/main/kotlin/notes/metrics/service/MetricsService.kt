package notes.metrics.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import notes.config.websocket.VoiceNotesWebSocketHandler
import notes.kafka.model.Metric
import notes.metrics.domain.MetricSource
import notes.ollama.client.OllamaPromptService
import notes.ollama.client.OllamaClient
import notes.ollama.model.OllamaGenerateRequestBody
import notes.kafka.model.VoiceNoteTranscribed
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service

class MetricsService(
    private val ollamaClient: OllamaClient,
    private val ollamaPromptService: OllamaPromptService,
    private val voiceNotesWebSocketHandler: VoiceNotesWebSocketHandler,
    private val kafkaTemplate: KafkaTemplate<String, Metric>,
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

        val metrics = getMetrics(original, MetricSource.ORIGINAL_TRANSCRIPT,
            voiceNoteTanscribed,
            rewritten)
        log.info("Metrics: $metrics")
        val rewrittenMetrics = getMetrics(rewritten, MetricSource.REWRITTEN_TRANSCRIPT,
            voiceNoteTanscribed,
            "N/A - this is the rewritten text")
        log.info("Rewritten Metrics: $rewrittenMetrics")

        publishMetricsToKafka(metrics)
        sendToWebsocket(voiceNoteTanscribed.voiceNoteFilename?:"", metrics)

        //TODO: publish metrics for rewritten text also

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
                           transcribedTextRewrite:String): List<Metric> {
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
                     transcribedTextRewrite:String): List<Metric> {
        return try {
            val cleanedJson = jsonString.trim()
                .removePrefix("```json")
                .removeSuffix("```")
                .trim()

            val objectMapper = jacksonObjectMapper()
            objectMapper.readValue(cleanedJson, Array<Metric>::class.java)
                .toList()
                .map{
                    Metric(type=it.type,
                        summary=it.summary,
                        llm=ollamaPromptService.getModelForMetricExtraction(),
                        source = source,
                        transcribedText=voiceNoteTanscribed.transcribedText,
                        transcribedTextRewrite=transcribedTextRewrite,
                        transcribedFilename = voiceNoteTanscribed.transcribedFilename,
                        voiceNoteFilename = voiceNoteTanscribed.voiceNoteFilename?:"")
                }
        } catch (e: Exception) {
            println("Error parsing JSON: ${e.message}")
            emptyList()
        }
    }

    private fun publishMetricsToKafka(metrics: List<Metric>) {

        metrics.forEach() {
            log.info("Sending metric to Kafka {}", it)
            val message: Message<Metric> = MessageBuilder
                .withPayload(it)
                .setHeader(KafkaHeaders.TOPIC, generatedMetricsTopic)
                .setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplate.send(message)
            log.info("Message sent with success")
        }
    }

    private fun sendToWebsocket(audioFilename: String, metrics: List<Metric>) {
        voiceNotesWebSocketHandler.sendMetricsUpdate(audioFilename, metrics)
    }

}