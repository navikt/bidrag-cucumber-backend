package no.nav.bidrag.cucumber.backend.dokument

import no.nav.bidrag.cucumber.RestTjeneste
import no.nav.bidrag.cucumber.ScenarioManager
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType

class RestTjenesteTestdata(alias: String) : RestTjeneste(alias) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RestTjenesteTestdata::class.java)
        private var slettTidligereTestdata = true
    }

    fun opprettJournalpost(journalpostJson: String) {
        if (slettTidligereTestdata) {
            slettOpprettedeData()
            slettTidligereTestdata = false
        }

        val correlationIdWithContentType = initHttpHeadersWithCorrelationIdAndEnhet()
        correlationIdWithContentType.contentType = MediaType.APPLICATION_JSON

        post("/journalpost", HttpEntity(journalpostJson, correlationIdWithContentType))
    }

    private fun slettOpprettedeData() {
            val endpointUrl = "/journal/slett/testdata"
            ScenarioManager.useScenarioForLogging = false
            val httpEntity = httpEntity(endpointUrl)
            exchange(httpEntity, endpointUrl, HttpMethod.DELETE)
            LOGGER.info("slettet testdata (ikke mottaksregistrert: $endpointUrl")
    }
}
