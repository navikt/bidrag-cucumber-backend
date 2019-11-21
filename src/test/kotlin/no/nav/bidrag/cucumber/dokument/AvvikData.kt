package no.nav.bidrag.cucumber.dokument

import no.nav.bidrag.cucumber.X_ENHETSNUMMER_HEADER
import org.springframework.http.HttpHeaders

data class AvvikData(
        var beskrivelse: String? = null,
        private var journalpostId: String? = null,
        val saksnummer: String,
        private val detaljer: MutableMap<String, String> = HashMap()
) {
    companion object {
        private val journalpostIdForAvvikstype: MutableMap<String, String> = HashMap()
    }

    lateinit var avvikstype: String
    lateinit var enhetsnummer: String

    constructor(saksnummer: String, journalpostId: String) : this(saksnummer = saksnummer) {
        this.journalpostId = journalpostId
    }

    fun hentAvvikshendelse(): String {
        return if (beskrivelse == null) """{"avvikType":"$avvikstype","enhetsnummer":"$enhetsnummer"}"""
        else if (detaljer.isEmpty()) """{"avvikType":"$avvikstype","enhetsnummer":"$enhetsnummer", "beskrivelse":"$beskrivelse"}"""
        else """{"avvikType":"$avvikstype","enhetsnummer":"$enhetsnummer","beskrivelse":"$beskrivelse","detaljer":{"${hentKey()}":"${hentValue()}"}}"""
    }

    private fun hentKey() = detaljer.keys.iterator().next()
    private fun hentValue() = detaljer.values.iterator().next()

    private fun hentDetaljerSomJson(): String {
        return "{" + detaljer.map { """"${it.key}":"${it.value}"""" } + "}"
    }

    fun leggTilEnhetsnummer(httpHeaders: HttpHeaders): HttpHeaders {
        httpHeaders[X_ENHETSNUMMER_HEADER] = enhetsnummer
        return httpHeaders
    }

    fun erForJournalpostId(journalpostId: String) = journalpostId.contains(Regex(journalpostIdForAvvikstype[avvikstype]!!))
    fun harIkkeJournalpostIdForAvvikstype() = !journalpostIdForAvvikstype.containsKey(avvikstype)
    fun lagEndepunktUrl() = "/sak/$saksnummer/journal/$journalpostId/avvik"
    fun lagEndepunktUrlForAvvikstype() = "/sak/$saksnummer/journal/${journalpostIdForAvvikstype[avvikstype]}/avvik"
    fun lagEndepunktUrlForOppgaveSok() = "?journalpostId=${hentJournalpostIdUtenPrefix()}&statuskategori=AAPEN"

    fun hentJournalpostId() = journalpostIdForAvvikstype[avvikstype]!!
    private fun hentJournalpostIdUtenPrefix() = journalpostIdForAvvikstype[avvikstype]!!.removePrefix("BID-")

    fun leggTilJournalpostIdForAvvikstype(journalpostId: String) {
        journalpostIdForAvvikstype[avvikstype] = journalpostId
    }

    fun harJournalpostId() = journalpostId != null

    fun leggTil(key: String, value: String) {
        detaljer[key] = value
    }
}
