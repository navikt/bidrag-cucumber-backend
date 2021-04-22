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

  @Når("jeg henter informasjon om saksbehandler med ident {string}")
  fun `jeg henter informasjon om saksbehandler med ident`(ident: String) {
    restTjeneste.exchangeGet("/saksbehandler/info/$ident")
  }

  @Når("jeg henter enheter for saksbehandler med ident {string}")
  fun `jeg henter enheter for saksbehandler med ident`(ident: String) {
    restTjeneste.exchangeGet("/saksbehandler/enhetsliste/$ident")
  }

  @Når("jeg henter journalfoerende enheter fra arbeidsfordeling")
  fun `jeg henter jounalfoerende enheter fra arbeidsfordeling`() {
    restTjeneste.exchangeGet("/arbeidsfordeling/enhetsliste/journalforende")
  }

  @Når("jeg henter enheter fra arbeidsfordeling for person med ident {string}")
  fun `jeg henter enheter fra arbeidsfordeling for person med ident`(ident: String) {
    restTjeneste.exchangeGet("/arbeidsfordeling/enhetsliste/geografisktilknytning/$ident")
  }
}
