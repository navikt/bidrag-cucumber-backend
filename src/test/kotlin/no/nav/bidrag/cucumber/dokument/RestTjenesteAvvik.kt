package no.nav.bidrag.cucumber.dokument

import no.nav.bidrag.cucumber.RestTjeneste
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType

class RestTjenesteAvvik(alias: String) : RestTjeneste(alias) {

    fun post(avvikData: AvvikData) {
        val headers = avvikData.leggTilEnhetsnummer(httpHeadersWithCorrelationId())
        headers.contentType = MediaType.APPLICATION_JSON_UTF8

        val avvikshendelse = HttpEntity(avvikData.hentAvvikshendelse(), headers)
        post(avvikData.endepunktUrl, avvikshendelse)
    }
}