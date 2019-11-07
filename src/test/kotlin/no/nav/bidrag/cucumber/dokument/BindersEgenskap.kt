package no.nav.bidrag.cucumber.dokument

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.BidragCucumberScenarioManager
import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.Fasit
import no.nav.bidrag.cucumber.FellesEgenskaper.Companion.restTjeneste
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertAll
import org.springframework.http.HttpStatus

class BindersEgenskap {

    companion object {
        lateinit var dokumentreferanse: DokumentReferanse
    }

    @Before
    fun `adminstrer scenario`(scenario: Scenario) {
        BidragCucumberScenarioManager.use(scenario)
    }

    @Når("jeg ber om tilgang til dokument på journalpostId {string} og dokumentreferanse {string}")
    fun `jeg ber om tilgang til dokument for en journalpost og dokumentreferanse`(journalpostId: String, dokumentreferanse: String) {
        restTjeneste.exchangeGet("/tilgang/$journalpostId/$dokumentreferanse")
        BindersEgenskap.dokumentreferanse = DokumentReferanse(dokumentreferanse)
    }

    @Og("dokument url skal være gyldig")
    fun `dokument url skal vaere gyldig`() {
        val response = restTjeneste.hentResponseSomMap()
        val url = response["dokumentUrl"] as String
        val brevserverResource = Fasit.hentFasitRessurs("alias=brevserverUrl", "type=baseurl", "environment=${Environment.fetch()}")

        assertAll(
                { assertThat(url).`as`("url proptocol").startsWith("mbdok://BI12@brevklient") },
                { assertThat(brevserverResource.url()).`as`("feil brevserver url").isNotNull() },
                { assertThat(url).`as`("mangler dokumentreferanse").contains(dokumentreferanse.hentMedErstattetSpesialtegn()) }
        )
    }
}
