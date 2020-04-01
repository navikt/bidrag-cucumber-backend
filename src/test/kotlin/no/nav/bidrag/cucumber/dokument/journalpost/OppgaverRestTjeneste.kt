package no.nav.bidrag.cucumber.dokument.journalpost

import no.nav.bidrag.cucumber.RestTjeneste
import no.nav.bidrag.cucumber.dokument.AvvikData
import no.nav.bidrag.cucumber.dokument.AvvikDataMottaksregistrertJp

class OppgaverRestTjeneste : RestTjeneste("oppgave.oppgaver") {
    fun finnOppgaverFor(avvikData: AvvikData) {
        exchangeGet(avvikData.lagEndepunktUrlForOppgaveSok())
    }

    fun finnOppgaverFor(avvikData: AvvikDataMottaksregistrertJp) {
        exchangeGet(avvikData.lagEndepunktUrlForOppgaveSok())
    }
}
