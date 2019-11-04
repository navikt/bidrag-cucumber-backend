package no.nav.bidrag.cucumber.dokument.journalpost

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.Fasit
import no.nav.bidrag.cucumber.dokument.DokumentReferanse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertAll
import org.springframework.http.HttpStatus

class BindersEgenskap {

    companion object {
        private lateinit var restTjeneste: RestTjenesteJournalpost
        private lateinit var dokumentreferanse: DokumentReferanse
    }

    @Gitt("resttjenesten bidragDokumentJournalpost for sjekking av tilgang")
    fun `resttjenesten bidragDokument`() {
        restTjeneste = RestTjenesteJournalpost()
    }

    @Og("jeg ber om tilgang til dokument {string}")
    fun `jeg ber om tilgang til dokument`(dokumentreferanse: String) {
        BindersEgenskap.dokumentreferanse = DokumentReferanse(dokumentreferanse)
        restTjeneste.exchangeGet("/tilgang/$dokumentreferanse")
    }

    @Så("skal http status for tilgangen være {string}")
    fun `skal statuskoden vaere`(kode: String) {
        val status = HttpStatus.valueOf(kode.toInt())
        assertThat(restTjeneste.hentHttpStatus()).isEqualTo(status)
    }

    @Og("returnert dokument url skal være gyldig")
    fun `dokument url skal vaere gyldig`() {
        val response = restTjeneste.hentResponseSomMap()
        val url = response["dokumentUrl"] as String
        val brevserverResource = Fasit.hentFasitRessurs("alias=brevserverUrl", "type=baseurl", "environment=${Environment.fetch()}")

        assertAll(
                { assertThat(url).`as`("mangler riktig proptocol").startsWith("mbdok://BI12@brevklient") },
                { assertThat(brevserverResource.url()).`as`("feil brevserver url").isNotNull() },
                { assertThat(url).`as`("mangler dokumentreferanse").contains(dokumentreferanse.hentMedErstattetSpesialtegn()) }
        )
    }
}