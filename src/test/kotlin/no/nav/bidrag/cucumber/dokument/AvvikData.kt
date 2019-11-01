package no.nav.bidrag.cucumber.dokument

import no.nav.bidrag.cucumber.X_ENHETSNUMMER_HEADER
import org.springframework.http.HttpHeaders

data class AvvikData(
        var beskrivelse: String? = null,
        val saksnummer: String,
        private val journalpostIdForAvvikstype: MutableMap<String, String> = HashMap()
) {
    lateinit var avvikstype: String
    lateinit var enhetsnummer: String
    lateinit var journalpostId: String

    constructor(saksnummer: String, journalpostId: String) : this(saksnummer = saksnummer) {
        this.journalpostId = journalpostId
    }

    fun hentAvvikshendelse(): String {
        return if (beskrivelse == null) "{\"avvikType\":\"$avvikstype\",\"enhetsnummer\":\"$enhetsnummer\"}"
        else "{\"avvikType\":\"$avvikstype\",\"enhetsnummer\":\"$enhetsnummer\", \"beskrivelse\":\"$beskrivelse\"}"
    }

    fun leggTilEnhetsnummer(httpHeaders: HttpHeaders): HttpHeaders {
        httpHeaders[X_ENHETSNUMMER_HEADER] = enhetsnummer
        return httpHeaders
    }

    fun harIkkeJournalpostIdForAvvikstype() = !journalpostIdForAvvikstype.containsKey(avvikstype)
    fun lagEndepunktUrl() = "/sak/$saksnummer/journal/$journalpostId/avvik"
    fun lagEndepunktUrlForAvvikstype() = "/sak/$saksnummer/journal/${journalpostIdForAvvikstype[avvikstype]}/avvik"

    fun leggTilJournalpostIdForAvvikstype(journalpostId: String) {
        journalpostIdForAvvikstype[avvikstype] = journalpostId
    }
}
