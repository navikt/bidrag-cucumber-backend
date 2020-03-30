package no.nav.bidrag.cucumber.dokument

import no.nav.bidrag.cucumber.FellesTestdataEgenskaper

data class AvvikDataMottaksregistrertJp(
        val avvikstype: String,
        val testdataNokkel: String
) {

    fun hentJournalpostId() = FellesTestdataEgenskaper.journalpostIdPerKey[testdataNokkel]!!
    fun lagEndepunktUrl(path: String) = path.replace(Regex("journalpostId"), hentJournalpostId())
}
