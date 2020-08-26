package no.nav.bidrag.cucumber.dokument

import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.Fasit
import no.nav.bidrag.cucumber.FellesEgenskaper.Companion.restTjeneste
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertAll

class BindersEgenskaper {

    companion object {
        lateinit var dokumentreferanse: DokumentReferanse
    }

    @Når("jeg ber om tilgang til dokument på journalpostId {string} og dokumentreferanse {string}")
    fun `jeg ber om tilgang til dokument for en journalpost og dokumentreferanse`(journalpostId: String, dokumentreferanse: String) {
        restTjeneste.exchangeGet("/tilgang/$journalpostId/$dokumentreferanse")
        BindersEgenskaper.dokumentreferanse = DokumentReferanse(dokumentreferanse)
    }

    @Og("dokument url skal være gyldig")
    fun `dokument url skal vaere gyldig`() {
        val response = restTjeneste.hentResponseSomMap()
        val url = response["dokumentUrl"] as String
        val brevserverResource = Fasit.hentFasitRessurs("alias=brevserverUrl", "type=baseurl", "environment=${Environment.fetchNamespace()}")

        assertAll(
                { assertThat(url).`as`("url proptocol").startsWith("mbdok://BI12@brevklient") },
                { assertThat(brevserverResource.url()).`as`("feil brevserver url").isNotNull() },
                { assertThat(url).`as`("mangler dokumentreferanse").contains(dokumentreferanse.hentMedErstattetSpesialtegn()) }
        )
    }
}
