package no.nav.bidrag.cucumber.arkivering

import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import junit.framework.Assert.assertTrue
import no.nav.bidrag.cucumber.FellesEgenskaper
import no.nav.bidrag.cucumber.FellesTestdataEgenskaper
import org.assertj.core.api.Assertions
import org.springframework.web.client.HttpServerErrorException

class ArkiveringEgenskap {

    @Når("jeg ber om at en journalpost uten dokument i brevlager, sendes til {string} i Joark via bidrag-dokument-arkiveringsendepunkt med sti {string}")
    fun `arkivere journalpost`(key: String, requestSti: String) {
        val journalpostId = FellesTestdataEgenskaper.journalpostIdPerKey[key]?.replace("BID-", "" as String)
        val path = requestSti.replace("{}", journalpostId as String)
        try {
            FellesEgenskaper.restTjeneste.exchangePost(path)
        } catch (e: HttpServerErrorException) {
            assertTrue(!e.message.isNullOrBlank())
        }
    }

    @Og("responsen skal inneholde et felt med navn {string}")
    fun `responsen skal inneholde et felt med navn`(key: String) {
        val responseObject = FellesEgenskaper.restTjeneste.hentResponseSomMap()
        @Suppress("UNCHECKED_CAST") val feltFraResponse = responseObject[key] as Map<String, Any>?
        val verdiFraResponse = feltFraResponse?.get(key)?.toString()

        Assertions.assertThat(verdiFraResponse).`as`("$key skal inneholde en verdi").isNotEqualTo(null)
    }
}