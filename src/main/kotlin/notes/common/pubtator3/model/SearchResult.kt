package notes.common.pubtator3.model

/**
 * 
 * @param id Unique identifier of the publication.
 * @param pmid PubMed ID of the publication.
 * @param title Title of the publication.
 * @param journal Journal where the publication is published.
 * @param authors 
 * @param date Date of publication in ISO 8601 format.
 * @param doi Digital Object Identifier (DOI) of the publication.
 * @param score Relevance score for the search result.
 * @param textHl Highlighted search result with matching terms.
 * @param citations 
 */
data class SearchResult (

    /* Unique identifier of the publication. */
    val _id: kotlin.String? = null,
    /* PubMed ID of the publication. */
    val pmid: kotlin.Int? = null,
    /* Title of the publication. */
    val title: kotlin.String? = null,
    /* Journal where the publication is published. */
    val journal: kotlin.String? = null,
    val authors: kotlin.Array<kotlin.String>? = null,
    /* Date of publication in ISO 8601 format. */
    val date: kotlin.String? = null,
    /* Digital Object Identifier (DOI) of the publication. */
    val doi: kotlin.String? = null,
    /* Relevance score for the search result. */
    val score: java.math.BigDecimal? = null,
    /* Highlighted search result with matching terms. */
    val textHl: kotlin.String? = null,
    val citations: Citations? = null
)