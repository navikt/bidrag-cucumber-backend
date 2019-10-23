package no.nav.bidrag.cucumber.dokument.journalpost

import io.cucumber.java.no.Gitt
import no.nav.bidrag.cucumber.RestTjenesteEgenskap

class BidragDokumentJournalpostEgenskap {

    private var restTjenesteEgenskap = RestTjenesteEgenskap()

    @Gitt("jeg henter journalpost for sak {string} med id {string}")
    fun `jeg henter journalpost for sak med id`(saksnummer: String, onlineJournalpostId: String) {
        restTjenesteEgenskap.exchangeGet("/sak/$saksnummer/journal/$onlineJournalpostId")
    }

    @Gitt("jeg henter journalposter for sak {string} med fagområde {string}")
    fun `jeg henter journalposter for sak med fagomrade`(saksnummer: String, fagomrade: String) {
        restTjenesteEgenskap.exchangeGet("/sakjournal/$saksnummer?fagomrade=$fagomrade")
    }

    @Gitt("jeg endrer journalpost for sak {string} med id {string} til:")
    fun `jeg endrer journalpost med id til`(saksnummer: String, journalpostId: String, journalpostJson: String) {
        restTjenesteEgenskap.put("/sak/$saksnummer/journal/$journalpostId", journalpostJson)
    }
}
