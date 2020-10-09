package no.nav.bidrag.cucumber.dokument

import no.nav.bidrag.cucumber.RestTjeneste
import no.nav.bidrag.cucumber.ScenarioManager
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType

class RestTjenesteTestdata(alias: String) : RestTjeneste(alias) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RestTjenesteTestdata::class.java)
        private var idForTestdataResponse: Map<String, Any>? = null
    }

    fun opprettJournalpost(journalpostJson: String) {
        if (idForTestdataResponse == null) {
            exchangeGet("journal/max/ids")
            idForTestdataResponse = hentResponseSomMap()
            ScenarioManager.log(">>>> $idForTestdataResponse <<<<")
        }

        val correlationIdWithContentType = initHttpHeadersWithCorrelationIdAndEnhet()
        correlationIdWithContentType.contentType = MediaType.APPLICATION_JSON

        post("/journalpost", HttpEntity(journalpostJson, correlationIdWithContentType))
    }

    fun slettOpprettedeData() {
        if (idForTestdataResponse != null) {
            val fraJpId = idForTestdataResponse?.get("maxJournalpostId")
            val fraJsakId = idForTestdataResponse?.get("maxJournalsakId")
            val endpointUrl = "journal/delete/fra-jp-id/$fraJpId/fra-jsak-id/$fraJsakId"
            ScenarioManager.useScenarioForLogging = false
            exchangeDelete(endpointUrl)
            LOGGER.info("slettet testdata (ikke mottaksregistrert: $endpointUrl")
        }
    }

    private fun exchangeDelete(endpointUrl: String) {
        val httpEntity = httpEntity(endpointUrl)
        exchange(httpEntity, endpointUrl, HttpMethod.DELETE)
    }
}