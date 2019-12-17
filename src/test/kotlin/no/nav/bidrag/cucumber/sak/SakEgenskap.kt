package no.nav.bidrag.cucumber.sak

import io.cucumber.java.no.Når
import no.nav.bidrag.cucumber.FellesEgenskaper
import no.nav.bidrag.cucumber.ScenarioManager

class SakEgenskap {

    @Når("jeg henter bidragssaker for person med fnr {string}")
    fun jeg_henter_bidragssaker_for_person_med_fnr(fnr: String) {
        FellesEgenskaper.restTjeneste.exchangeGet("/person/sak/$fnr")
    }

    @Når("jeg henter pip for sak {string}")
    fun jeg_henter_pip_for_sak(saksnummer: String) {
        FellesEgenskaper.restTjeneste.exchangeGet("/pip/sak/$saksnummer")
    }
}
