package no.nav.bidrag.cucumber.dokument

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.BidragCucumberScenarioManager
import no.nav.bidrag.cucumber.FellesEgenskaper
import no.nav.bidrag.cucumber.FellesEgenskaper.Companion.restTjeneste
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.springframework.http.HttpStatus

class JournalposterEgenskap {

    @Before
    fun `administrer bidrag cucumber backend`(scenario: Scenario) {
        BidragCucumberScenarioManager.use(scenario)
    }

    @Gitt("resttjenesten bidragDokument til testing av journalposter")
    fun `resttjenesten bidragDokument`() {
        restTjeneste = RestTjenesteDokument()
    }

    @Så("skal http status for testen være {string}")
    fun `skal http status vaere`(enHttpStatus: String) {
        val httpStatus = HttpStatus.valueOf(enHttpStatus.toInt())

        assertThat(restTjeneste.hentHttpStatus()).isEqualTo(httpStatus)
    }

    @Og("responsen skal inneholde {string} = {string}")
    fun `responsen skal inneholde`(key: String, value: String) {
        val responseObject = restTjeneste.hentResponseSomMap()

        assertThat(responseObject[key]).`as`("json response (${restTjeneste.hentResponse()})").isEqualTo(value)
    }

    @Gitt("jeg henter journalposter for sak {string} som har fagområde {string} ned bidragDokument")
    fun `jeg henter journalposter for sak som har fagomrade`(saksnummer: String, fagomrade: String) {
        restTjeneste.exchangeGet("/sakjournal/$saksnummer?fagomrade=$fagomrade")
    }

    @Og("så skal responsen være en liste")
    fun `skal responsen vaere en liste`() {
        assertThat(restTjeneste.hentResponse()?.trim()).startsWith("[")
    }

    @Gitt("jeg henter journalpost for sak {string} som har id {string} med bidragDokument")
    fun `jeg henter journalpost for sak som har id`(saksnummer: String, journalpostId: String) {
        restTjeneste.exchangeGet("/sak/$saksnummer/journal/$journalpostId")
    }

    @Og("så skal responsen være et objekt")
    fun `skal responsen vaere et objekt`() {
        assertThat(restTjeneste.hentResponse()?.trim()).startsWith("{").isNotEqualTo("{}")
    }

    @Og("følgende properties skal ligge i responsen:")
    fun `folgende properties skal ligge i responsen`(properties: List<String>) {
        val verifyer = SoftAssertions()
        val responseObject = restTjeneste.hentResponseSomMap()

        properties.forEach { verifyer.assertThat(responseObject).`as`("missing $it in jp: ${responseObject["journalpostId"]})").containsKey(it) }

        verifyer.assertAll()
    }

    @Gitt("jeg endrer journalpost for sak {string} som har id {string} for bidragDokument:")
    fun `jeg endrer journalpost for sak som har id`(saksnummer: String, journalpostId: String, json: String) {
            restTjeneste.put("/sak/$saksnummer/journal/$journalpostId", json)
    }
}
