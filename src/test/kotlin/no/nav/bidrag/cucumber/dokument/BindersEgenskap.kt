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
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus

class BindersEgenskap {

    companion object {
        lateinit var restTjeneste: RestTjenesteDokument
    }

    @Before
    fun `adminstrer scenario`(scenario: Scenario) {
        BidragCucumberScenarioManager.use(scenario)
    }

    @Gitt("resttjenesten bidragDokument")
    fun `resttjenesten bidragDokument`() {
        restTjeneste = RestTjenesteDokument()
    }

    @Når("jeg ber om tilgang til dokument på journalpostId {string} og dokumentreferanse {string}")
    fun `jeg ber om tilgang til dokument for en journalpost og dokumentreferanse`(journalpostId: String, dokumentreferanse: String) {
        restTjeneste.exchangeGet("/tilgang/$journalpostId/$dokumentreferanse")
    }

    @Så("skal statuskoden være {string}")
    fun `skal statuskoden vaere`(kode: String) {
        val status = HttpStatus.valueOf(kode.toInt())
        assertThat(restTjeneste.hentHttpStatus()).isEqualTo(status)
    }

    @Og("dokument url skal være gyldig")
    fun `dokument url skal vaere gyldig`() {
        val response = restTjeneste.hentResponseSomMap()
        val url = response["dokumentUrl"] as String
        val brevserverResource = Fasit.hentFasitRessurs("alias=brevserverUrl", "type=baseurl", "environment=${Environment.fetch()}")

        assertThat(url).`as`("url proptocol").startsWith("mbdok://BI12@brevklient")
        assertThat(brevserverResource.url()).`as`("brevserver url").isNotNull()
    }
}
