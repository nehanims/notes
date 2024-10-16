package notes.common.ollama.model


/**
 * ollama API
 * API for interacting with ollama models
 *
 * OpenAPI spec version: 1.0.0
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


/**
 *
 * @param model
 * @param createdAt
 * @param response
 * @param done
 * @param totalDuration
 * @param loadDuration
 * @param sampleCount
 * @param sampleDuration
 * @param promptEvalCount
 * @param promptEvalDuration
 * @param evalCount
 * @param evalDuration
 * @param context
 */
data class OllamaGenerateResponseBody (

    val model: String? = null,
    val createdAt: java.time.LocalDateTime? = null,
    val response: String = "",
    val done: Boolean? = null,
    val totalDuration: Int? = null,
    val loadDuration: Int? = null,
    val sampleCount: Int? = null,
    val sampleDuration: Int? = null,
    val promptEvalCount: Int? = null,
    val promptEvalDuration: Int? = null,
    val evalCount: Int? = null,
    val evalDuration: Int? = null,
    val context: Array<Int>? = null
)