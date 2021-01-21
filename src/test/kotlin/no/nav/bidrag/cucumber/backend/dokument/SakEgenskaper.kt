package no.nav.bidrag.cucumber.backend.dokument

import io.cucumber.java.no.Når
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.backend.FellesEgenskaper.Companion.restTjeneste
import org.assertj.core.api.SoftAssertions

class SakEgenskaper {

    @Når("jeg henter journalposter for sak {string} som har fagområde {string}")
    fun `jeg henter journalposter for sak med fagomrade`(saksnummer: String, fagomrade: String) {
        restTjeneste.exchangeGet("/sak/$saksnummer/journal?fagomrade=$fagomrade")
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
