package no.nav.bidrag.cucumber.dokument

import io.cucumber.java.no.Gitt

class DatabaseEgenskap {
    val restTjenesteForManipuleringAvDatabase = RestTjenesteTestdata()

    @Gitt("data på journalpost med id {string} inneholder:")
    fun `data pa journalpost med id inneholder`(journalpostId: String, json: String) {
        restTjenesteForManipuleringAvDatabase.put("/journalpost/$journalpostId", json)
    }
}
