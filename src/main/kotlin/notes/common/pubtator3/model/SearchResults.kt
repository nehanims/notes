package notes.common.pubtator3.model

/**
 *
 * @param results
 * @param facets
 * @param pageSize Number of results per page.
 * @param current Current page number.
 * @param count Total number of search results.
 * @param totalPages Total number of pages.
 */
data class SearchResults (

    val results: kotlin.Array<notes.common.pubtator3.model.SearchResult>? = null,
    val facets: Facets? = null,
    /* Number of results per page. */
    val pageSize: kotlin.Int? = null,
    /* Current page number. */
    val current: kotlin.Int? = null,
    /* Total number of search results. */
    val count: kotlin.Int? = null,
    /* Total number of pages. */
    val totalPages: kotlin.Int? = null
)