package notes.common.pubtator3.model

/**
 *
 * @param id Unique identifier of the bioconcept.
 * @param biotype Type of bioconcept (e.g., disease, gene, chemical).
 * @param name Official name of the bioconcept.
 * @param match Information about how the match was made.
 */
data class EntityAutocomplete (
    /* Unique identifier of the bioconcept. */
    val _id: kotlin.String? = null,
    /* Type of bioconcept (e.g., disease, gene, chemical). */
    val biotype: kotlin.String? = null,
    /* Official name of the bioconcept. */
    val name: kotlin.String? = null,
    /* Information about how the match was made. */
    val match: kotlin.String? = null
)