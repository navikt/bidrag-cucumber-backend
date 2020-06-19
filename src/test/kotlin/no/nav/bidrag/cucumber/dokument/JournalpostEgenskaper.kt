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

    @Gitt("jeg henter journalpost for sak {string} som har id {string} uten api endring")
    fun `jeg henter journalpost for sak som har id uten api endring`(saksnummer: String, journalpostId: String) {
        restTjeneste.exchangeGet("/sak/$saksnummer/journal/$journalpostId")
    }

    @Og("følgende properties skal ligge i responsen:")
    fun `folgende properties skal ligge i responsen`(properties: List<String>) {
        val verifyer = SoftAssertions()
        val responseObject = restTjeneste.hentResponseSomMap()

        properties.forEach { verifyer.assertThat(responseObject).`as`("missing $it in jp: ${responseObject["journalpostId"]})").containsKey(it) }

        verifyer.assertAll()
    }

    @Gitt("jeg endrer journalpost for sak {string} som har id {string} for bidragDokument:")
    fun `jeg endrer journalpost for sak som har id`(saksnummer: String, journalpostId: String, json: String) {
        restTjeneste.exchangePut("/sak/$saksnummer/journal/$journalpostId", json)
    }

    @Gitt("jeg endrer journalpost med id {string} til:")
    fun `jeg endrer journalpost med id til`(journalpostId: String, journalpostJson: String) {
        restTjeneste.exchangePut("/journal/$journalpostId", journalpostJson)
    }
}
