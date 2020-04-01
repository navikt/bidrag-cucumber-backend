package no.nav.bidrag.cucumber.dokument.journalpost

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.dokument.AvvikDataMottaksregistrertJp
import no.nav.bidrag.cucumber.dokument.AvvikEgenskaper
import no.nav.bidrag.cucumber.dokument.AvvikEgenskaperForMottaksregistrertJp
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus

class OppgaveEgenskaper {
    companion object {
        private lateinit var restTjenesteOppgaver: OppgaverRestTjeneste
    }

    @Gitt("jeg søker etter oppgaver for journalpost")
    fun `jeg soker etter oppgaver for journalpost`() {
        restTjenesteOppgaver = OppgaverRestTjeneste()
        restTjenesteOppgaver.finnOppgaverFor(AvvikEgenskaper.avvikData)
    }

    @Gitt("jeg søker etter oppgaver for mottaksregistrert journalpost")
    fun `jeg soker etter oppgaver for mottaksregistrert journalpost`() {
        restTjenesteOppgaver = OppgaverRestTjeneste()
        restTjenesteOppgaver.finnOppgaverFor(AvvikEgenskaperForMottaksregistrertJp.avvikData)
    }

    @Så("skal http status for oppgavesøket være {string}")
    fun `skal http status for oppgavesoket vaere`(kode: String) {
        val httpStatus = HttpStatus.valueOf(kode.toInt())

        assertThat(restTjenesteOppgaver.hentHttpStatus()).isEqualTo(httpStatus)
    }

    @Og("søkeresultatet skal inneholde en oppgave")
    fun `sokeresultatet skal inneholde en oppgave`() {
        val response = restTjenesteOppgaver.hentResponse()

        assertThat(response).contains("\"antallTreffTotalt\":1")
    }
}
