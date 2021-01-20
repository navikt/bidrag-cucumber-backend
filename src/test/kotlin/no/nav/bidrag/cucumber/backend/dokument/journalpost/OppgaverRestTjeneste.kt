package no.nav.bidrag.cucumber.backend.dokument.journalpost

import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.sikkerhet.Fasit
import no.nav.bidrag.cucumber.RestTjeneste
import no.nav.bidrag.cucumber.backend.dokument.AvvikData
import no.nav.bidrag.cucumber.backend.dokument.AvvikDataMottaksregistrertJp

class OppgaverRestTjeneste : RestTjeneste("oppgave.oppgaver", Fasit.hentRessurs("alias=oppgave.oppgaver", "type=restservice", "environment=${Environment.namespace}")) {
    fun finnOppgaverFor(avvikData: AvvikData) {
        exchangeGet(avvikData.lagEndepunktUrlForOppgaveSok())
    }

    fun finnOppgaverFor(avvikData: AvvikDataMottaksregistrertJp) {
        exchangeGet(avvikData.lagEndepunktUrlForOppgaveSok())
    }
}
