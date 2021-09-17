package no.nav.bidrag.cucumber.backend.dokument

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.backend.FellesEgenskaper
import no.nav.bidrag.cucumber.backend.FellesTestdataEgenskaper

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
        FellesEgenskaper.restTjeneste.exchangePatch(path + journalpostId, endreJsonCommand)
    }

    @Og("jeg registrerer endring av opprettet journalpost, {string}, med path {string}, med enhet {string}:")
    fun `jeg registrerer endring av journalpost med http api`(nokkelTilOpprettedeTestData: String, requestSti: String, endreJsonCommand: String, enhet: String) {
        val journalpostId = FellesTestdataEgenskaper.journalpostIdPerKey[nokkelTilOpprettedeTestData]
        val path = requestSti.removeSuffix("{}") + journalpostId
        FellesEgenskaper.restTjeneste.exchangePatch(path, enhet, endreJsonCommand)
    }
}
