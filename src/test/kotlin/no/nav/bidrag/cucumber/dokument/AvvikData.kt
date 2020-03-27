package no.nav.bidrag.cucumber.dokument

import no.nav.bidrag.cucumber.FellesTestdataEgenskaper
import no.nav.bidrag.cucumber.X_ENHET_HEADER
import org.springframework.http.HttpHeaders

data class AvvikData(
        var beskrivelse: String? = null,
        private var journalpostId: String? = null,
        val saksnummer: String,
        private val detaljer: MutableMap<String, String> = HashMap()
) {
    lateinit var avvikstype: String
    lateinit var enhet: String

    constructor(saksnummer: String, journalpostId: String) : this(saksnummer = saksnummer) {
        this.journalpostId = journalpostId
    }

    fun hentAvvikshendelse(): String {
        return if (beskrivelse == null) """{"avvikType":"$avvikstype","enhetsnummer":"$enhet"}"""
        else if (detaljer.isEmpty()) """{"avvikType":"$avvikstype","enhetsnummer":"$enhet", "beskrivelse":"$beskrivelse"}"""
        else """{"avvikType":"$avvikstype","enhetsnummer":"$enhet","beskrivelse":"$beskrivelse","detaljer":{"${hentKey()}":"${hentValue()}"}}"""
    }

    private fun hentKey() = detaljer.keys.iterator().next()
    private fun hentValue() = detaljer.values.iterator().next()

    fun leggTilEnhetsnummer(httpHeaders: HttpHeaders): HttpHeaders {
        httpHeaders[X_ENHET_HEADER] = enhet
        return httpHeaders
    }

    fun erForJournalpostId(journalpostId: String) = journalpostId.contains(Regex(FellesTestdataEgenskaper.journalpostIdPerKey[avvikstype]!!))
    fun lagEndepunktUrl() = "/sak/$saksnummer/journal/$journalpostId/avvik"
    fun lagEndepunktUrlForAvvikstype() = "/sak/$saksnummer/journal/${FellesTestdataEgenskaper.journalpostIdPerKey[avvikstype]}/avvik"
    fun lagEndepunktUrlForOppgaveSok() = "?journalpostId=${hentJournalpostIdUtenPrefix()}&statuskategori=AAPEN"

    fun hentJournalpostId() = FellesTestdataEgenskaper.journalpostIdPerKey[avvikstype]!!
    private fun hentJournalpostIdUtenPrefix() = FellesTestdataEgenskaper.journalpostIdPerKey[avvikstype]!!.removePrefix("BID-")

    fun harJournalpostId() = journalpostId != null

    fun leggTil(key: String, value: String) {
        detaljer[key] = value
    }
}
