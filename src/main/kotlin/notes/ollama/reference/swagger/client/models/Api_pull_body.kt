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
 * @param name Name of the model to pull (required)
 * @param insecure Allow insecure connections to the library
 */
data class ApiPullBody (

    /* Name of the model to pull (required) */
    val name: kotlin.String? = null,
    /* Allow insecure connections to the library */
    val insecure: kotlin.Boolean? = null
) {
}