package no.nav.bidrag.cucumber.dokument

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.BidragCucumberScenarioManager
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus

class JournalposterEgenskap {

    companion object{
        lateinit var restTjeneste: RestTjenesteDokument
    }

    @Before
    fun `administrer bidrag cucumber backend`(scenario: Scenario) {
        BidragCucumberScenarioManager.use(scenario)
    }

    @Gitt("resttjenesten bidragDokument til testing av journalposter")
    fun `resttjenesten bidragDokument`() {
       restTjeneste = RestTjenesteDokument()
    }

    @Når("jeg kaller helsetjenesten")
    fun `jeg kaller helsetjenesten`() {
        restTjeneste.exchangeGet("/actuator/health")
    }

    @Så("skal http status for testen være {string}")
    fun `skal http status vaere`(enHttpStatus: String) {
        val httpStatus = HttpStatus.valueOf(enHttpStatus.toInt())

        assertThat(restTjeneste.hentHttpStatus()).isEqualTo(httpStatus)
    }

    @Og("helsestatus skal inneholde {string} = {string}")
    fun `helsestatus skal inneholde`(key: String, value: String) {
        val responseObject = restTjeneste.hentResponseSomMap()

        assertThat(responseObject[key]).`as`("json response (${restTjeneste.hentResponse()})").isEqualTo(value)
    }
}
