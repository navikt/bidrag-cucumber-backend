package no.nav.bidrag.cucumber.backend.sak

import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.CREDENTIALS_PIP_AUTH
import no.nav.bidrag.cucumber.CREDENTIALS_PIP_USER
import no.nav.bidrag.cucumber.backend.FellesEgenskaper

class SakEgenskap {

    companion object {
        lateinit var pipUser: String
        lateinit var pipAuthentication: String
    }

    @Når("jeg henter bidragssaker for person med fnr {string}")
    fun jeg_henter_bidragssaker_for_person_med_fnr(fnr: String) {
        FellesEgenskaper.restTjeneste.exchangeGet("/person/sak/$fnr")
    }

    @Og("bruk av en produksjonsbruker med tilgang til bidrag-sak pip")
    fun `bruk av en produksjonsbruker med tilgang til bidrag sak pip`() {
        pipUser = System.getProperty(CREDENTIALS_PIP_USER) ?: throw IllegalStateException("Fant ikke pip bruker (ie srvbisys)")
        pipAuthentication = System.getProperty(CREDENTIALS_PIP_AUTH) ?: throw IllegalStateException("Fant ikke passord til pip bruker")
    }

    @Når("jeg henter pip for sak {string}")
    fun jeg_henter_pip_for_sak(saksnummer: String) {
        FellesEgenskaper.restTjeneste.exchangeGet("/pip/sak/$saksnummer", pipUser, pipAuthentication)
    }
}
