package no.nav.bidrag.cucumber.backend.dokument

import no.nav.bidrag.cucumber.backend.FellesTestdataEgenskaper
import no.nav.bidrag.cucumber.X_ENHET_HEADER
import org.springframework.http.HttpHeaders
import java.util.stream.Collectors

data class AvvikData(
        var beskrivelse: String? = null,
        var journalpostId: String? = null,
        val saksnummer: String?,
        private val detaljer: MutableMap<String, String> = HashMap()
) {
    lateinit var avvikstype: String
    lateinit var enhet: String

    constructor(saksnummer: String, journalpostId: String) : this(saksnummer = saksnummer) {
        this.journalpostId = journalpostId
    }

    fun hentAvvikshendelse(): String {
        var jsonBeskrivelse = ""
        var jsonDetaljer = ""
        var jsonSak = ""

        if (beskrivelse != null) {
            jsonBeskrivelse = ""","beskrivelse":"$beskrivelse""""
        }

        if (detaljer.isNotEmpty()) {
            jsonDetaljer = ""","detaljer":{${lagRaderAvDetaljer()}}"""
        }

        if (saksnummer != null) {
            jsonSak = ""","saksnummer":"$saksnummer" """
        }

        return """{"avvikType":"$avvikstype"$jsonBeskrivelse$jsonDetaljer$jsonSak}"""
    }

    private fun lagRaderAvDetaljer(): String {
        return detaljer.keys.stream()
                .map { key -> """"$key":"${detaljer[key]}"""" }
                .collect(Collectors.joining(","))
    }

    fun leggTilEnhetsnummer(httpHeaders: HttpHeaders): HttpHeaders {
        httpHeaders[X_ENHET_HEADER] = enhet
        return httpHeaders
    }

    fun erForJournalpostId(journalpostId: String) = journalpostId.contains(Regex(FellesTestdataEgenskaper.journalpostIdPerKey.getValue(avvikstype)))
    fun lagEndepunktUrl() = "/journal/$journalpostId/avvik"
    fun lagEndepunktUrlForAvvikstype() = "/journal/${FellesTestdataEgenskaper.journalpostIdPerKey[avvikstype]}/avvik"
    fun lagEndepunktUrlForNokkel(nokkel: String) = "/journal/${FellesTestdataEgenskaper.journalpostIdPerKey[nokkel]}/avvik"
    fun lagEndepunktUrlForHentAvvik() = "/journal/${FellesTestdataEgenskaper.journalpostIdPerKey[avvikstype]}/avvik?saksnummer=$saksnummer"
    fun lagEndepunktUrlForHentAvvikFor(nokkel: String) = "/journal/${FellesTestdataEgenskaper.journalpostIdPerKey[nokkel]}/avvik?saksnummer=$saksnummer"
    fun lagEndepunktUrlForOppgaveSok() = "?journalpostId=${hentJournalpostIdUtenPrefix()}&statuskategori=AAPEN"

    fun hentJournalpostId() = FellesTestdataEgenskaper.journalpostIdPerKey[avvikstype]!!
    private fun hentJournalpostIdUtenPrefix() = FellesTestdataEgenskaper.journalpostIdPerKey[avvikstype]!!.removePrefix("BID-")

    fun harJournalpostId() = journalpostId != null

    fun leggTil(key: String, value: String) {
        detaljer[key] = value
    }

}
