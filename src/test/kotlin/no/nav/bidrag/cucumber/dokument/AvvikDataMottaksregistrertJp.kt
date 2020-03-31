package no.nav.bidrag.cucumber.dokument

import no.nav.bidrag.cucumber.FellesTestdataEgenskaper

data class AvvikDataMottaksregistrertJp(
        val avvikstype: String,
        val testdataNokkel: String,
        var enhet: String? = null
) {
    fun lagEndepunktUrl(path: String) = path.replace(Regex("journalpostId"), hentJournalpostId())
    private fun hentJournalpostId() = FellesTestdataEgenskaper.journalpostIdPerKey[testdataNokkel]!!
}
