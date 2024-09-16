package notes.pubtator3

import notes.common.pubtator3.client.PubTator3Client
import notes.common.pubtator3.model.SearchResults
import notes.common.pubtator3.model.EntityAutocomplete
import notes.common.pubtator3.model.EntityRelation
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/research/pubtator3-api/")
class PubTator3Controller (private val pubtator3Client: PubTator3Client){
    val log = LoggerFactory.getLogger(PubTator3Controller::class.java)

    /**
     * Find Entity ID by Query and Concept
     * This endpoint allows users to search for entity identifiers based on a free text query. Optionally,
     * users can specify a bioconcept type and limit the number of returned identifiers.
     * @param query Free text query to search for bioconcept identifiers.
     * @param concept Optional bioconcept type to filter results. Can be one of the following: &#x60;gene&#x60;, &#x60;disease&#x60;, &#x60;chemical&#x60;, &#x60;species&#x60;.  (optional)
     * @param limit Optional parameter to limit the number of results returned. If not specified, the default limit is applied.  (optional)
     * @return List<EntityAutocomplete>
     */
    @GetMapping("/entity/autocomplete/")
    fun findEntityId(
        @RequestParam query: String,
        @RequestParam(required = false) concept: String? = null,
        @RequestParam(required = false) limit: Int? = null
    ): List<EntityAutocomplete> {
        return pubtator3Client.findEntityId(query, concept, limit)
    }

    /**
     * Search Publications by Text, Entity ID, or Relations
     * Query through this API to retrieve relevant publications returned by PubTator3.
     * The query can be in the form of free text, entity ID (e.g., @CHEMICAL_remdesivir), or relations
     * between entities. Query relations can be specified as relations between two entities
     * (e.g., relations:ANY|@CHEMICAL_Doxorubicin|@DISEASE_Neoplasms) or relations between one entity and
     * entities of a specific type (e.g., relations:ANY|@CHEMICAL_Doxorubicin|DISEASE).
     *
     * @param text The free-text query or entity ID to search for.
     * @param page The page number for paginated results (default is 1). (optional, default to 1)
     * @return SearchResults
     */
    @GetMapping("/search/")
    fun exportSearchResults(
        @RequestParam text: String,
        @RequestParam(required = false) page: Int? = null
    ): SearchResults{
        val result = pubtator3Client.exportSearchResults(text, page)
        log.info("Search results: $result")
        return result
    }

    /**
     * Find Related Entities
     * This API allows users to find related entities by providing an &#x60;entityId&#x60; (found through \&quot;Find Entity ID\&quot;).
     * Optionally, users can filter results by &#x60;relation_type&#x60; and &#x60;entity_type&#x60;.
     * @param e1 The entity ID to query related entities for.
     * @param type The relation type to filter by (optional). (optional)
     * @param e2 The entity type to filter related entities by (optional). (optional)
     * @return List<RelatedEntity>
     */
    @GetMapping("/relations")
    fun findRelatedEntities(
        @RequestParam e1: String,
        @RequestParam(required = false) type: String? = null,
        @RequestParam(required = false) e2: String? = null
    ): List<EntityRelation>{
        return pubtator3Client.findRelatedEntities(e1, type, e2)
    }

}
