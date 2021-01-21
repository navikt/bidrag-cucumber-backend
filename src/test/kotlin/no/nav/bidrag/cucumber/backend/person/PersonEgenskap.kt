package no.nav.bidrag.cucumber.backend.person

import io.cucumber.java.no.Når
import no.nav.bidrag.cucumber.backend.FellesEgenskaper.Companion.restTjeneste

class PersonEgenskap {

    @Når("jeg henter informasjon for ident {string}")
    fun `jeg henter informasjon for ident`(ident: String) {
        restTjeneste.exchangeGet("/informasjon/$ident")
    }
}