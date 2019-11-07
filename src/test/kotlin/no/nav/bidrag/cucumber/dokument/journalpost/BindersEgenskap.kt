package no.nav.bidrag.cucumber.dokument.journalpost

import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.Fasit
import no.nav.bidrag.cucumber.FellesEgenskaper.Companion.restTjeneste
import no.nav.bidrag.cucumber.dokument.DokumentReferanse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertAll

class BindersEgenskap {

    companion object {
        private lateinit var dokumentreferanse: DokumentReferanse
    }

    @Og("jeg ber om tilgang til dokument {string}")
    fun `jeg ber om tilgang til dokument`(dokumentreferanse: String) {
        BindersEgenskap.dokumentreferanse = DokumentReferanse(dokumentreferanse)
        restTjeneste.exchangeGet("/tilgang/$dokumentreferanse")
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