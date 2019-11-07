package no.nav.bidrag.cucumber.dokument

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.BidragCucumberScenarioManager
import no.nav.bidrag.cucumber.FellesEgenskaper.Companion.restTjeneste
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.springframework.http.HttpStatus

class SakEgenskap {

    @Before
    fun `administrer bidrag cucumber backend`(scenario: Scenario) {
        BidragCucumberScenarioManager.use(scenario)
    }

    @Når("jeg henter journalposter for sak {string} som har fagområde {string} med bidragDokument")
    fun `jeg henter journalposter for sak med fagomrade`(saksnummer: String, fagomrade: String) {
        restTjeneste.exchangeGet("/sakjournal/$saksnummer?fagomrade=$fagomrade")
    }

    @Så("hver journal i listen skal ha {string} = {string}")
    fun `hvert journal i listen skal ha`(key: String, value: String) {
        val verifyer = SoftAssertions()
        val responseObject = restTjeneste.hentResponseSomListe()

        responseObject.forEach {
            verifyer.assertThat(it.get(key)).`as`("id: ${it.get("journalpostId")}").isEqualTo(value)
        }

        verifyer.assertAll()
    }

    @Suppress("UNCHECKED_CAST")
    @Så("hver journal i listen skal ha objektet {string} med følgende properties:")
    fun `hvert journal i listen skal ha objekt med properties`(objektNavn: String, properties: List<String>) {
        val verifyer = SoftAssertions()
        val responseObject = restTjeneste.hentResponseSomListe()

        responseObject.forEach { jp ->
            val objekt = jp[objektNavn] as Map<String, *>
            properties.forEach { verifyer.assertThat(objekt).`as`("id: ${jp.get("journalpostId")}").containsKey(it) }
        }

        verifyer.assertAll()
    }
}
