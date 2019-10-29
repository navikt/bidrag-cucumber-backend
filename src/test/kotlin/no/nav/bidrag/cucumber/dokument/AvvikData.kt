package no.nav.bidrag.cucumber.dokument

import no.nav.bidrag.cucumber.X_ENHETSNUMMER_HEADER
import org.springframework.http.HttpHeaders

data class AvvikData(
        val endepunktUrl: String,
        var avvikstype: String = "ikke satt",
        var enhetsnummer: String = "ikke satt",
        var beskrivelse: String? = null
) {
    fun hentAvvikshendelse(): String {
        return if (beskrivelse == null) "{\"avvikType\":\"$avvikstype\",\"enhetsnummer\":\"$enhetsnummer\"}"
        else "{\"avvikType\":\"$avvikstype\",\"enhetsnummer\":\"$enhetsnummer\", \"beskrivelse\":\"$beskrivelse\"}"
    }

    fun leggTilEnhetsnummer(httpHeaders: HttpHeaders): HttpHeaders {
        httpHeaders[X_ENHETSNUMMER_HEADER] = enhetsnummer
        return httpHeaders
    }
}