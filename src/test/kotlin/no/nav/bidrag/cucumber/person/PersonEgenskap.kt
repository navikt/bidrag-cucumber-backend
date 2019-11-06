package no.nav.bidrag.cucumber.person

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.RestTjeneste
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus

class PersonEgenskap {
    companion object {
        private lateinit var restTjeneste: RestTjeneste
    }

    @Gitt("resttjenesten bidragPerson")
    fun `resttjenesten bidragPerson`() {
        restTjeneste = RestTjeneste("bidragPerson")
    }

    @Når("jeg henter informasjon for ident {string}")
    fun `jeg henter informasjon for ident`(ident: String) {
        restTjeneste.exchangeGet("/informasjon/$ident")
    }

    @Så("skal http status fra bidragPerson være {string}")
    fun `skal http status fra bidragPerson vaere`(kode: String) {
        val httpStatus = HttpStatus.valueOf(kode.toInt())

        assertThat(restTjeneste.hentHttpStatus()).isEqualTo(httpStatus)
    }
}