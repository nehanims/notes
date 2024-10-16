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
 * @param name Name of the model to create (required)
 * @param path Path to the Modelfile
 */
data class ApiCreateBody (

    /* Name of the model to create (required) */
    val name: kotlin.String? = null,
    /* Path to the Modelfile */
    val path: kotlin.String? = null
) {
}