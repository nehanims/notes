
package notes.common.pubtator3.model

/**
 * 
 * @param journal 
 * @param year 
 */
data class Facets (

    val journal: kotlin.Array<FacetsJournal>? = null,
    val year: kotlin.Array<FacetsYear>? = null
) {
}