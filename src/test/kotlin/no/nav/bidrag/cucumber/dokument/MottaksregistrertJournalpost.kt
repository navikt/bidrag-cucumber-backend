package no.nav.bidrag.cucumber.dokument

import io.cucumber.java.no.Gitt
import no.nav.bidrag.cucumber.FellesEgenskaper
import no.nav.bidrag.cucumber.FellesTestdataEgenskaper

class MottaksregistrertJournalpost {
    @Gitt("at jeg henter journalpost med path {string}")
    fun `at jeg_henter_journalpost_med_path`(path: String) {
        FellesEgenskaper.restTjeneste.exchangeGet(path)
    }

    @Gitt("at jeg henter opprettet journalpost for {string} med http-api {string}")
    fun `at jeg henter opprettet journalpost for`(key: String, requestSti: String) {
        val journalpostId = FellesTestdataEgenskaper.journalpostIdPerKey[key]
        val path = requestSti.removeSuffix("{}")
        FellesEgenskaper.restTjeneste.exchangeGet(path + journalpostId)
    }
}
