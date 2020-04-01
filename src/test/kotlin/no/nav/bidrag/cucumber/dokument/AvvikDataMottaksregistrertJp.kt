package no.nav.bidrag.cucumber.dokument

import no.nav.bidrag.cucumber.FellesTestdataEgenskaper

data class AvvikDataMottaksregistrertJp(
        val avvikstype: String,
        val testdataNokkel: String,
        var enhet: String? = null
) {
    fun lagEndepunktUrl(path: String) = path.replace(Regex("journalpostId"), hentJournalpostId())
    private fun hentJournalpostId() = FellesTestdataEgenskaper.journalpostIdPerKey[testdataNokkel]!!
    fun lagEndepunktUrlForOppgaveSok() = "?journalpostId=${hentJournalpostIdUtenPrefix()}&statuskategori=AAPEN"
    private fun hentJournalpostIdUtenPrefix() = FellesTestdataEgenskaper.journalpostIdPerKey[testdataNokkel]!!.removePrefix("BID-")
}
