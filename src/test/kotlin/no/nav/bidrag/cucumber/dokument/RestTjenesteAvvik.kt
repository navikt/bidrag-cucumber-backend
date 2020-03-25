package no.nav.bidrag.cucumber.dokument

import no.nav.bidrag.cucumber.RestTjeneste
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
        val headers = avvikData.leggTilEnhetsnummer(initHttpHeadersWithCorrelationId())
        headers.contentType = MediaType.APPLICATION_JSON

        return HttpEntity(avvikData.hentAvvikshendelse(), headers)
    }

    fun hentResponseSomListeAvStrenger() = ArrayList(
            (hentResponse() as String)
                    .removePrefix("[")
                    .removeSuffix("]")
                    .split(",")
    )
}