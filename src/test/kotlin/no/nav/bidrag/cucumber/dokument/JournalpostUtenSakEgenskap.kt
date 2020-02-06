package no.nav.bidrag.cucumber.dokument

import io.cucumber.java.no.Gitt
import no.nav.bidrag.cucumber.FellesEgenskaper

class JournalpostUtenSakEgenskap {
    @Gitt("at jeg henter journalpost med path {string}")
    fun `at jeg_henter_journalpost_med_path`(path: String) {
        FellesEgenskaper.restTjeneste.exchangeGet(path)
    }
}
