package no.nav.bidrag.cucumber.dokument.journalpost

import io.cucumber.java.no.Gitt
import no.nav.bidrag.cucumber.CacheRestTjeneste
import no.nav.bidrag.cucumber.RestTjeneste
import no.nav.bidrag.cucumber.RestTjenesteEgenskap

class BidragDokumentJournalpostEgenskap {

    private var restTjenesteEgenskap = RestTjenesteEgenskap()
    private lateinit var testdataRestTjeneste: RestTjeneste

    @Gitt("jeg henter journalpost for sak {string} med id {string}")
    fun `jeg henter journalpost for sak med id`(saksnummer: String, onlineJournalpostId: String) {
        restTjenesteEgenskap.exchangeGet("/sak/$saksnummer/journal/$onlineJournalpostId")
    }

    @Gitt("jeg henter journalposter for sak {string} med fagområde {string}")
    fun `jeg henter journalposter for sak med fagomrade`(saksnummer: String, fagomrade: String) {
        restTjenesteEgenskap.exchangeGet("/sakjournal/$saksnummer?fagomrade=$fagomrade")
    }

    @Gitt("jeg endrer journalpost med id {string} til:")
    fun `jeg endrer journalpost med id til`(journalpostId: String, journalpostJson: String) {
        CacheRestTjeneste.hent("bidragDokumentTestdata").put("/journalpoost/$journalpostId", journalpostJson)
    }
}
