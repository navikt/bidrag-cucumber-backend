package no.nav.bidrag.cucumber.person

import io.cucumber.java.no.Når
import no.nav.bidrag.cucumber.FellesEgenskaper.Companion.restTjeneste

class PersonEgenskap {

    @Når("jeg henter informasjon for ident {string}")
    fun `jeg henter informasjon for ident`(ident: String) {
        restTjeneste.exchangeGet("/informasjon/$ident")
    }
}