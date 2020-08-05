package no.nav.bidrag.cucumber.dokument

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.FellesEgenskaper
import no.nav.bidrag.cucumber.FellesTestdataEgenskaper

class JournalpostEgenskaperPathUtenSak {
    @Gitt("at jeg henter journalpost med path {string}")
    fun `at jeg_henter_journalpost_med_path`(path: String) {
        FellesEgenskaper.restTjeneste.exchangeGet(path)
    }

    @Gitt("at jeg henter opprettet journalpost for {string} med path {string}")
    @Og("at jeg henter endret journalpost for {string} med path {string}")
    fun `at jeg henter opprettet journalpost for`(key: String, requestSti: String) {
        val journalpostId = FellesTestdataEgenskaper.journalpostIdPerKey[key]
        val path = requestSti.replace("{}", journalpostId as String)
        FellesEgenskaper.restTjeneste.exchangeGet(path)
    }

    @Og("jeg registrerer endring av opprettet journalpost, {string}, med path {string}:")
    fun `jeg registrerer endring av journalpost med http api`(nokkelTilOpprettedeTestData: String, requestSti: String, endreJsonCommand: String) {
        val journalpostId = FellesTestdataEgenskaper.journalpostIdPerKey[nokkelTilOpprettedeTestData]
        val path = requestSti.removeSuffix("{}")
        FellesEgenskaper.restTjeneste.exchangePut(path + journalpostId, endreJsonCommand)
    }

    @Og("jeg registrerer endring av opprettet journalpost, {string}, med path {string}, med enhet {string}:")
    fun `jeg registrerer endring av journalpost med http api`(nokkelTilOpprettedeTestData: String, requestSti: String, endreJsonCommand: String, enhet: String) {
        val journalpostId = FellesTestdataEgenskaper.journalpostIdPerKey[nokkelTilOpprettedeTestData]
        val path = requestSti.removeSuffix("{}") + journalpostId
        FellesEgenskaper.restTjeneste.exchangePut(path, enhet, endreJsonCommand)
    }
}
