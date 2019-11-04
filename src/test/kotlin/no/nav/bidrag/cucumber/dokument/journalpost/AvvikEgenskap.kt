package no.nav.bidrag.cucumber.dokument.journalpost

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.BidragCucumberScenarioManager
import no.nav.bidrag.cucumber.dokument.AvvikData
import no.nav.bidrag.cucumber.dokument.RestTjenesteAvvik
import no.nav.bidrag.cucumber.dokument.RestTjenesteTestdata
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus

class AvvikEgenskap {
    companion object Managed {
        private lateinit var avvikData: AvvikData
        private lateinit var restTjenesteAvvik: RestTjenesteAvvik
        private lateinit var restTjenesteOppgaver: OppgaverRestTjeneste
    }

    @Before
    fun `use scenario`(scenario: Scenario) {
        BidragCucumberScenarioManager.use(scenario)
    }

    @Gitt("resttjenesten bidragDokumentJournalpost for avviksbehandling")
    fun `gitt resttjenesten bidragDokumenJournalpost`() {
        restTjenesteAvvik = RestTjenesteAvvik("bidragDokumentJournalpost")
    }

    @Og("saksnummer {string} for avviksbehandling av {string}")
    fun `saksnummer for avviksbehandling`(saksnummer: String, avvikstype: String) {
        avvikData = AvvikData(saksnummer = saksnummer)
        avvikData.avvikstype = avvikstype
    }

    @Når("jeg ber om gyldige avviksvalg med bidragDokumentJournalpost")
    fun `jeg ber om gyldige avviksvalg med bidragDokumentJournalpost`() {
        restTjenesteAvvik.exchangeGet(avvikData.lagEndepunktUrlForAvvikstype())
    }

    @Så("skal http status for avviksbehandlingen være {string}")
    fun `skal http status for avviksbehandlingen vaere`(kode: String) {
        val httpStatus = HttpStatus.valueOf(kode.toInt())

        assertThat(restTjenesteAvvik.hentHttpStatus()).isEqualTo(httpStatus)
    }

    @Gitt("enhetsnummeret {string} til avviksbehandlingen")
    fun `enhetsnummeret til avviksbehandlingen`(enhetsnummer: String) {
        avvikData.enhetsnummer = enhetsnummer
    }

    @Og("opprett journalpost og ta vare på journalpostId:")
    fun `opprett journalpost og ta vare pa journalpostId`(jpJson: String) {
        if (avvikData.harIkkeJournalpostIdForAvvikstype()) {
            val restTjenesteTestdata = RestTjenesteTestdata()

            restTjenesteTestdata.opprettJournalpost(jpJson)
            assertThat(restTjenesteTestdata.hentHttpStatus()).isEqualTo(HttpStatus.CREATED)

            val opprettetJpMap = restTjenesteTestdata.hentResponseSomMap()
            avvikData.leggTilJournalpostIdForAvvikstype(opprettetJpMap["journalpostId"] as String)
        }
    }

    @Og("listen med avvikstyper skal inneholde {string}")
    fun `listen med avvikstyper skal inneholde`(avvikstype: String) {
        val funnetAvvikstyper = restTjenesteAvvik.hentResponseSomListeAvStrenger()

        assertThat(funnetAvvikstyper).contains("\"$avvikstype\"")
    }

    @Og("listen med avvikstyper skal ikke inneholde {string}")
    fun `listen med avvikstyper skal ikke inneholde`(avvikstype: String) {
        val funnetAvvikstyper = restTjenesteAvvik.hentResponseSomListeAvStrenger()

        assertThat(funnetAvvikstyper).doesNotContain("\"$avvikstype\"")
    }

    @Gitt("beskrivelsen {string}")
    fun beskrivelsen(beskrivelse: String) {
        avvikData.beskrivelse = beskrivelse
    }

    @Når("jeg oppretter avvik med bidragDokumentJournalpost")
    fun jeg_oppretter_avviket() {
        restTjenesteAvvik.opprettAvvikForAvvikstype(avvikData)
    }

    @Gitt("jeg søker etter oppgaver for journalpost")
    fun `jeg soker etter oppgaver for journalpost`() {
        restTjenesteOppgaver = OppgaverRestTjeneste()
        restTjenesteOppgaver.finnOppgaverFor(avvikData)
    }

    @Så("skal http status for oppgavesøket være {string}")
    fun `skal http status for oppgavesoket_vaere`(kode: String) {
        val httpStatus = HttpStatus.valueOf(kode.toInt())

        assertThat(restTjenesteOppgaver.hentHttpStatus()).isEqualTo(httpStatus)
    }

    @Og("søkeresultatet skal inneholde en oppgave")
    fun `sokeresultatet skal inneholde en oppgave`() {
        val response = restTjenesteOppgaver.hentResponse()

        assertThat(response).contains("\"antallTreffTotalt\":1")
    }
}
