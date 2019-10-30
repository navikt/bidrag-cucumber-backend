package no.nav.bidrag.cucumber.dokument

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.BidragCucumberScenarioManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
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

    @Gitt("jeg henter journalposter for sak {string} med fagområde {string} i bidrag-dokument")
    fun `jeg henter journalposter for sak med fagomrade`(saksnummer: String, fagomrade: String) {
        restTjeneste.exchangeGet("/sakjournal/$saksnummer?fagomrade=$fagomrade")
    }

    @Og("så skal responsen være en liste")
    fun `skal responsen vaere en liste`() {
        assertThat(restTjeneste.hentResponse()?.trim()).startsWith("[")
    }

    @Og("hvert element i listen skal ha følgende properties satt:")
    fun `hvert element i listen skal ha folgende properties satt`(properties: List<String>) {
        val verifyer = SoftAssertions()
        val responseObject = restTjeneste.hentResponseSomListe()

        responseObject.forEach { element ->
            properties.forEach { verifyer.assertThat(element).`as`("missing $it in jp: ${element["journalpostId"]})").containsKey(it) }
        }

        verifyer.assertAll()
    }
}
