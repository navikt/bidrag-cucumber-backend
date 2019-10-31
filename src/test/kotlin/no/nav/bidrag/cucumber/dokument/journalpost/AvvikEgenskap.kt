package no.nav.bidrag.cucumber.dokument.journalpost

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.RestTjeneste
import no.nav.bidrag.cucumber.dokument.RestTjenesteAvvik
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus

class AvvikEgenskap {
    companion object Managed {
        lateinit var avviksbehandling: String
        private val opprettetJournalpostIdForAvvik: MutableMap<String, String> = HashMap()
        private lateinit var restTjenesteAvvik: RestTjenesteAvvik
        private lateinit var saksnummer: String
    }

    @Gitt("resttjenesten bidragDokumentJournalpost for avviksbehandling")
    fun `gitt resttjenesten bidragDokumenJournalpost`() {
        restTjenesteAvvik = RestTjenesteAvvik("bidragDokumentJournalpost")
    }

    @Og("saksnummer {string} for avviksbehandling av {string}")
    fun `saksnummer for avviksbehandling`(saksnummer: String, avviksbehandling: String) {
        Managed.saksnummer = saksnummer
        Managed.avviksbehandling = avviksbehandling
    }

    @Når("jeg ber om gyldige avviksvalg med bidragDokumentJournalpost")
    fun `jeg ber om gyldige avviksvalg med bidragDokumentJournalpost`() {
        restTjenesteAvvik.exchangeGet("/sak/$saksnummer/journal/BID-${opprettetJournalpostIdForAvvik[avviksbehandling]}/avvik")
    }

    @Så("skal http status for avviksbehandlingen være {string}")
    fun `skal http status for avviksbehandlingen vaere`(kode: String) {
        val httpStatus = HttpStatus.valueOf(kode.toInt())

        assertThat(restTjenesteAvvik.hentHttpStatus()).isEqualTo(httpStatus)
    }

    @Og("opprettet, samt cachet journalpost:")
    fun `opprettet samt cachet journalpost`(jpJson: String) {
        if (!opprettetJournalpostIdForAvvik.containsKey(avviksbehandling)) {
            val restTjenesteTestdata = RestTjeneste("bidragDokumentTestdata")
            restTjenesteTestdata.post("/journalpost", HttpEntity(jpJson))
            assertThat(restTjenesteTestdata.hentHttpStatus()).isEqualTo(HttpStatus.CREATED)

            val opprettetJpMap = restTjenesteTestdata.hentResponseSomMap()
            opprettetJournalpostIdForAvvik[avviksbehandling] = opprettetJpMap["journalpostId"] as String
        }
    }
}