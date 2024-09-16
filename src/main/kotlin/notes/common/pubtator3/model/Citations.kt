
package notes.common.pubtator3.model


/**
 * Citation details in different formats.
 * @param nLM National Library of Medicine citation format.
 * @param bibTeX Citation in BibTeX format.
 */
data class Citations (

    /* National Library of Medicine citation format. */
    val nLM: kotlin.String? = null,
    /* Citation in BibTeX format. */
    val bibTeX: kotlin.String? = null
) {
}