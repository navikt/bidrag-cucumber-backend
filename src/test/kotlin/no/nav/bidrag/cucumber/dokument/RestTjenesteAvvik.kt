package no.nav.bidrag.cucumber.dokument

import no.nav.bidrag.cucumber.FellesTestdataEgenskaper
import no.nav.bidrag.cucumber.RestTjeneste
import no.nav.bidrag.cucumber.ScenarioManager
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType

class RestTjenesteAvvik(alias: String) : RestTjeneste(alias) {

    fun opprettAvvik(avvikData: AvvikData) {
        if (avvikData.harJournalpostId()) {
            post(avvikData.lagEndepunktUrl(), initEntityMedHeaders(avvikData))
        } else {
            opprettAvvikForAvvikstype(avvikData)
        }
    }

    private fun opprettAvvikForAvvikstype(avvikData: AvvikData) {
        post(avvikData.lagEndepunktUrlForAvvikstype(), initEntityMedHeaders(avvikData))
    }

    private fun initEntityMedHeaders(avvikData: AvvikData): HttpEntity<String> {
        val headers = avvikData.leggTilEnhetsnummer(initHttpHeadersWithCorrelationIdAndEnhet())
        headers.contentType = MediaType.APPLICATION_JSON

        val avvikshendelse = avvikData.hentAvvikshendelse()
        ScenarioManager.log("avvikshendelse", avvikshendelse)

        return HttpEntity(avvikshendelse, headers)
    }

    fun hentResponseSomListeAvStrenger(): List<String> {
        if (responseEntity.body == null) {
            return emptyList()
        }

        return ArrayList(
                (responseEntity.body as String)
                        .removePrefix("[")
                        .removeSuffix("]")
                        .split(",")
        )
    }

    fun opprettAvvik(avvikData: AvvikData, nokkel: String) {
        if (FellesTestdataEgenskaper.journalpostIdPerKey.containsKey(nokkel)) {
            avvikData.erForJournalpostId(FellesTestdataEgenskaper.journalpostIdPerKey.getValue(nokkel))
            opprettAvvikForAvvikstype(avvikData)
        } else {
            throw IllegalStateException("Har ikke opprettet journalpost for nokkel: $nokkel")
        }
    }
}
