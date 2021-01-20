package no.nav.bidrag.cucumber.backend.organisasjon

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import no.nav.bidrag.cucumber.backend.FellesEgenskaper.Companion.restTjeneste
import no.nav.bidrag.cucumber.RestTjeneste

class OrganisasjonEgenskap {

    @Gitt("resttjenesten bidragOrganisasjon")
    fun `resttjenesten bidragOrganisasjon`() {
        restTjeneste = RestTjeneste("bidragOrganisasjon")
    }

    @Når("jeg henter informasjon for ldap ident {string}")
    fun `jeg henter informasjon for ldap ident`(ldapIdent: String) {
        restTjeneste.exchangeGet("/saksbehandler/info/$ldapIdent")
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
