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
package io.swagger.client.models


/**
 * 
 * @param source The source model name (required)
 * @param destination The destination model name (required)
 */
data class ApiCopyBody (

    /* The source model name (required) */
    val source: kotlin.String? = null,
    /* The destination model name (required) */
    val destination: kotlin.String? = null
) {
}