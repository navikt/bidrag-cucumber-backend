package no.nav.bidrag.cucumber.backend.dokument

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.backend.FellesEgenskaper.Companion.restTjeneste
import no.nav.bidrag.cucumber.backend.FellesTestdataEgenskaper
import org.assertj.core.api.SoftAssertions

class JournalpostEgenskaper {

    @Gitt("jeg henter journalpost for sak {string} som har id {string}")
    fun `jeg henter journalpost for sak som har id`(saksnummer: String, journalpostId: String) {
        restTjeneste.exchangeGet("/journal/$journalpostId?saksnummer=$saksnummer")
    }

    @Gitt("jeg henter journalpost for sak {string} som har id for nokkel {string}")
    fun `jeg henter journalpost for sak og id for nokkel`(saksnummer: String, nokkel: String) {
        val journalpostId = FellesTestdataEgenskaper.journalpostIdPerKey[nokkel]
        restTjeneste.exchangeGet("/journal/BID-$journalpostId?saksnummer=$saksnummer")
    }

    @Og("følgende properties skal ligge i responsen:")
    fun `folgende properties skal ligge i responsen`(properties: List<String>) {
        val verifyer = SoftAssertions()
        val responseObject = restTjeneste.hentResponseSomMap()

        properties.forEach { verifyer.assertThat(responseObject).`as`("missing $it in jp: ${responseObject["journalpostId"]})").containsKey(it) }

        verifyer.assertAll()
    }

    @Gitt("jeg endrer journalpost som har id {string}:")
    fun `jeg endrer journalpost som har id`(journalpostId: String, json: String) {
        restTjeneste.exchangePut("/journal/$journalpostId", json)
    }

    @Gitt("jeg endrer journalpost for testdata med nøkkel {string}:")
    fun `jeg endrer journalpost for testdata nokkel`(nokkel: String, json: String) {
        val journalpostId = FellesTestdataEgenskaper.journalpostIdPerKey[nokkel]
        restTjeneste.exchangePut("/journal/$journalpostId", json)
    }

    @Gitt("jeg endrer journalpost med id {string} til:")
    fun `jeg endrer journalpost med id til`(journalpostId: String, journalpostJson: String) {
        restTjeneste.exchangePut("/journal/$journalpostId", journalpostJson)
    }
}
