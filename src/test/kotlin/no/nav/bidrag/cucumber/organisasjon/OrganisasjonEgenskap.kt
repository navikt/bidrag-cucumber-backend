package no.nav.bidrag.cucumber.organisasjon

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.RestTjeneste
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus

class OrganisasjonEgenskap {

    companion object {
        private lateinit var restTjeneste: RestTjeneste
    }

    @Gitt("resttjenesten bidragOrganisasjon")
    fun `resttjenesten bidragOrganisasjon`() {
        restTjeneste = RestTjeneste("bidragOrganisasjon")
    }

    @Når("jeg henter informasjon for ldap ident {string}")
    fun `jeg henter informasjon for ldap ident`(ldapIdent: String) {
        restTjeneste.exchangeGet("/saksbehandler/info/$ldapIdent")
    }

    @Så("skal http status fra bidragOrganisasjon være {string}")
    fun `skal http status fra bidragOrganisasjon vaere`(kode: String) {
        val httpStatus = HttpStatus.valueOf(kode.toInt())

        assertThat(restTjeneste.hentHttpStatus()).isEqualTo(httpStatus)
    }

    @Når("jeg henter enheter for saksbehandler med ident {string}")
    fun `jeg henter enheter for saksbehandler med ident`(ident: String) {
        restTjeneste.exchangeGet("/saksbehandler/enhetsliste/$ident")
    }

    @Når("jeg henter enheter for arbeidsfordeling med diskresjonskode {string} og geografisk tilknytning {string}")
    fun `jeg henter enheter for arbeidsfordeling med diskresjonskode og geografisk tilknytning`(diskresjonskode: String, geografiskTilknytning: String) {
        restTjeneste.exchangeGet("/arbeidsfordeling/enhetsliste/?diskresjonskode=$diskresjonskode&geografiskTilknytning=$geografiskTilknytning")
    }
}
