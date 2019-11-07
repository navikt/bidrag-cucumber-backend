package no.nav.bidrag.cucumber.dokument

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Når
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.BidragCucumberScenarioManager
import no.nav.bidrag.cucumber.RestTjeneste
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus
import java.util.EnumSet

class ApplikasjonEgenskap  {
    companion object {
        lateinit var restTjeneste: RestTjeneste
    }

    @Before
    fun `sett cucumber scenario og initier RestTjeneste`(scenario: Scenario) {
        BidragCucumberScenarioManager.use(scenario)
        restTjeneste = RestTjeneste("bidragDokument")
    }

    @Når("det gjøres et kall til {string}")
    fun `det gjores et kall til`(endpointUrl: String) {
        restTjeneste.exchangeGet(endpointUrl)
    }

    @Så("skal http status ikke være {string} eller {string}")
    fun `skal http status ikke vaere`(enHttpStatus: String, enAnnenHttpStatus: String) {
        val httpStatus = HttpStatus.valueOf(enHttpStatus.toInt())
        val annenHttpStatus = HttpStatus.valueOf(enAnnenHttpStatus.toInt())

        assertThat(restTjeneste.hentHttpStatus()).`as`("HttpStatus for " + restTjeneste.hentEndpointUrl())
                .isNotIn(EnumSet.of(httpStatus, annenHttpStatus))
    }
}
