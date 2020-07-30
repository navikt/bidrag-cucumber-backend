package no.nav.bidrag.cucumber.hendelse

import io.cucumber.java.no.Når
import no.nav.bidrag.cucumber.FellesEgenskaper

class HendelseEgenskap {

    @Når("jeg henter bidraghendelser for sak med saksnummer {string}")
    fun jeg_henter_bidragssaker_for_person_med_fnr(saksnummer: String) {
        FellesEgenskaper.restTjeneste.exchangeGet("/hendelse/sak/$saksnummer")
    }


}
