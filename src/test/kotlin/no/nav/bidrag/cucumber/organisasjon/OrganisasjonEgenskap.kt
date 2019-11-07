package no.nav.bidrag.cucumber.organisasjon

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.BidragCucumberScenarioManager
import no.nav.bidrag.cucumber.FellesEgenskaper.Companion.restTjeneste
import no.nav.bidrag.cucumber.RestTjeneste
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus

class OrganisasjonEgenskap {

    @Before
    fun `administrer bidrag cucumber backend`(scenario: Scenario) {
        BidragCucumberScenarioManager.use(scenario)
    }

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
