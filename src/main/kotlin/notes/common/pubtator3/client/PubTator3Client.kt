package notes.common.pubtator3.client

import notes.common.pubtator3.model.EntityAutocomplete
import notes.common.pubtator3.model.EntityRelation
import notes.common.pubtator3.model.SearchResults
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange


@HttpExchange("/research/pubtator3-api/")
public interface PubTator3Client {

    @GetExchange("/entity/autocomplete/")
    fun findEntityId(
        @RequestParam query: String,
        @RequestParam(required = false) concept: String? = null,
        @RequestParam(required = false) limit: Int? = null
    ): List<EntityAutocomplete>

    @GetExchange("/search/")
    fun exportSearchResults(
        @RequestParam text: String,
        @RequestParam(required = false) page: Int? = null
    ): SearchResults

    @GetExchange("/relations")
    fun findRelatedEntities(
        @RequestParam e1: String,
        @RequestParam(required = false) type: String? = null,
        @RequestParam(required = false) e2: String? = null
    ): List<EntityRelation>

}

