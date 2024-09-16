/**
 * PubTator3 API
 * This API provides functionality to retrieve biomedical text annotations and related entities. 
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
package notes.common.pubtator3.reference.client.models


/**
 * 
 * @param id Unique identifier of the bioconcept.
 * @param biotype Type of bioconcept (e.g., disease, gene, chemical).
 * @param name Official name of the bioconcept.
 * @param match Information about how the match was made.
 */
data class InlineResponse200 (

    /* Unique identifier of the bioconcept. */
    val id: kotlin.String? = null,
    /* Type of bioconcept (e.g., disease, gene, chemical). */
    val biotype: kotlin.String? = null,
    /* Official name of the bioconcept. */
    val name: kotlin.String? = null,
    /* Information about how the match was made. */
    val match: kotlin.String? = null
) {
}