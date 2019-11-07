package no.nav.bidrag.cucumber.dokument

import io.cucumber.java.no.Gitt
import no.nav.bidrag.cucumber.RestTjeneste

class DatabaseEgenskap {
    val restTjenesteForManipuleringAvDatabase = RestTjeneste("bidragDokumentTestdata")

    @Gitt("data på journalpost med id {string} inneholder:")
    fun `data pa journalpost med id inneholder`(journalpostId: String, json: String) {
        restTjenesteForManipuleringAvDatabase.put("/journalpost/$journalpostId", json)
    }
}
