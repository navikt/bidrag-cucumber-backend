package no.nav.bidrag.cucumber.dokument

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.FellesEgenskaper.Companion.restTjeneste
import org.assertj.core.api.SoftAssertions

class JournalpostEgenskaper {

    @Gitt("jeg henter journalpost for sak {string} som har id {string}")
    fun `jeg henter journalpost for sak som har id`(saksnummer: String, journalpostId: String) {
        restTjeneste.exchangeGet("/journal/$journalpostId?saksnummer=$saksnummer")
    }

    @Og("følgende properties skal ligge i responsen:")
    fun `folgende properties skal ligge i responsen`(properties: List<String>) {
        val verifyer = SoftAssertions()
        val responseObject = restTjeneste.hentResponseSomMap()

        properties.forEach { verifyer.assertThat(responseObject).`as`("missing $it in jp: ${responseObject["journalpostId"]})").containsKey(it) }

        verifyer.assertAll()
    }

    @Gitt("jeg endrer journalpost som har id {string} for bidragDokument:")
    fun `jeg endrer journalpost som har id`(journalpostId: String, json: String) {
        restTjeneste.exchangePut("/journal/$journalpostId", json)
    }

    @Gitt("jeg endrer journalpost med id {string} til:")
    fun `jeg endrer journalpost med id til`(journalpostId: String, journalpostJson: String) {
        restTjeneste.exchangePut("/journal/$journalpostId", journalpostJson)
    }
}
