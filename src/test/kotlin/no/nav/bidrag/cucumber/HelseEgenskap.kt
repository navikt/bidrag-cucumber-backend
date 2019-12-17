package no.nav.bidrag.cucumber

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.FellesEgenskaper.Companion.restTjeneste
import org.assertj.core.api.Assertions.assertThat

class HelseEgenskap {

    @Gitt("resttjenesten {string} for sjekk av helsedata")
    fun `resttjenesten for sjekk av helsedata`(alias: String) {
        restTjeneste = RestTjeneste(alias)
    }

    @Når("jeg kaller helsetjenesten")
    fun `jeg kaller helsetjenesten`() {
        restTjeneste.exchangeGet("/actuator/health")
    }

    @Og("header {string} skal være {string}")
    fun `header skal vaere`(headerName: String, headerValue: String) {
        val headere = restTjeneste.hentHttpHeaders()

        assertThat(headere[headerName]?.first()).isEqualTo(headerValue)
    }
}