package no.nav.bidrag.cucumber.dokument.journalpost

import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.Fasit
import no.nav.bidrag.cucumber.RestTjeneste
import no.nav.bidrag.cucumber.dokument.AvvikData
import no.nav.bidrag.cucumber.dokument.AvvikDataMottaksregistrertJp

class OppgaverRestTjeneste : RestTjeneste("oppgave.oppgaver", Fasit.hentFasitRessurs("alias=oppgave.oppgaver", "type=restservice", "environment=${Environment.namespace}")) {
    fun finnOppgaverFor(avvikData: AvvikData) {
        exchangeGet(avvikData.lagEndepunktUrlForOppgaveSok())
    }

    fun finnOppgaverFor(avvikData: AvvikDataMottaksregistrertJp) {
        exchangeGet(avvikData.lagEndepunktUrlForOppgaveSok())
    }
}
