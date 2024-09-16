package notes.common.pubtator3.model

/**
 *
 * @param type Type of relationship (e.g., negative_correlate, treat).
 * @param source The source entity ID.
 * @param target The target entity ID.
 * @param publications Number of publications that mention this relationship.
 */
data class EntityRelation (

    /* Type of relationship (e.g., negative_correlate, treat). */
    val type: kotlin.String? = null,
    /* The source entity ID. */
    val source: kotlin.String? = null,
    /* The target entity ID. */
    val target: kotlin.String? = null,
    /* Number of publications that mention this relationship. */
    val publications: kotlin.Int? = null
)