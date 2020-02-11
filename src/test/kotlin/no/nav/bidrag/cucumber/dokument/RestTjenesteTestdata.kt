package no.nav.bidrag.cucumber.dokument

import no.nav.bidrag.cucumber.RestTjeneste
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType

class RestTjenesteTestdata(alias: String): RestTjeneste(alias) {
    fun opprettJournalpost(journalpostJson: String) {
        val correlationIdWithContentType = initHttpHeadersWithCorrelationId()
        correlationIdWithContentType.contentType = MediaType.APPLICATION_JSON_UTF8

        post("/journalpost", HttpEntity(journalpostJson, correlationIdWithContentType))
    }

}