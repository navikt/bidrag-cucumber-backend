package no.nav.bidrag.cucumber.dokument

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.RestTjeneste
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus

class HelseEgenskap {
    companion object {
        private lateinit var restTjeneste: RestTjeneste
    }

    @Gitt("resttjenesten {string} for sjekk av helsedata")
    fun `resttjenesten for sjekk av helsedata`(alias: String) {
        restTjeneste = RestTjeneste(alias)
    }

    @Når("jeg kaller helsetjenesten")
    fun `jeg kaller helsetjenesten`() {
        restTjeneste.exchangeGet("/actuator/health")
    }

    @Så("skal http status for helsesjekken være {string}")
    fun `skal http status for helsesjekken vaere`(kode: String) {
        val httpStatus = HttpStatus.valueOf(kode.toInt())

        assertThat(restTjeneste.hentHttpStatus()).isEqualTo(httpStatus)
    }

    @Og("helseresponsen skal inneholde {string} = {string}")
    fun `helseresponsen skal inneholde`(key: String, value: String) {
        val responseObject = restTjeneste.hentResponseSomMap()

        assertThat(responseObject[key]).`as`("json response (${restTjeneste.hentResponse()})").isEqualTo(value)
    }
}