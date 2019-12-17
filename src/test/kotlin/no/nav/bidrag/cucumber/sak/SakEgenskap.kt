package no.nav.bidrag.cucumber.sak

import io.cucumber.java.no.Når
import no.nav.bidrag.cucumber.FellesEgenskaper

class SakEgenskap {

    @Når("jeg henter bidragssaker for person med fnr {string}")
    fun jeg_henter_bidragssaker_for_person_med_fnr(fnr: String) {
        FellesEgenskaper.restTjeneste.exchangeGet("/person/sak/$fnr")
    }
}