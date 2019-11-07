package no.nav.bidrag.cucumber.dokument.journalpost

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Gitt
import no.nav.bidrag.cucumber.BidragCucumberScenarioManager
import no.nav.bidrag.cucumber.FellesEgenskaper.Companion.restTjeneste

class BidragDokumentJournalpostEgenskap {

    @Before
    fun `sett cucumber scenario`(scenario: Scenario) {
        BidragCucumberScenarioManager.use(scenario)
    }

    @Gitt("resttjenesten bidragDokumentJournalpost")
    fun `gitt resttjenesten bidragDokumenJournalpost`() {
        restTjeneste = RestTjenesteJournalpost()
    }

    @Gitt("jeg henter journalpost for sak {string} med id {string}")
    fun `jeg henter journalpost for sak med id`(saksnummer: String, journalpostId: String) {
        restTjeneste.exchangeGet("/sak/$saksnummer/journal/$journalpostId")
    }

    @Gitt("jeg henter journalposter for sak {string} med fagområde {string}")
    fun `jeg henter journalposter for sak med fagomrade`(saksnummer: String, fagomrade: String) {
        restTjeneste.exchangeGet("/sakjournal/$saksnummer?fagomrade=$fagomrade")
    }

    @Gitt("jeg endrer journalpost for sak {string} med id {string} til:")
    fun `jeg endrer journalpost med id til`(saksnummer: String, journalpostId: String, journalpostJson: String) {
        restTjeneste.put("/sak/$saksnummer/journal/$journalpostId", journalpostJson)
    }
}
