# language: no
@bidrag-dokument-journalpost
Egenskap:  bidrag-dokument-journalpost (/tilgang REST API)

  Tester tilgang URL

  Scenario: Sjekk at vi får en gyldig URL for dokument tilgang
    Gitt resttjenesten bidragDokumentJournalpost for sjekking av tilgang
    Og jeg ber om tilgang til dokument 'dokref'
    Så skal http status for tilgangen være '200'
    Og returnert dokument url skal være gyldig
