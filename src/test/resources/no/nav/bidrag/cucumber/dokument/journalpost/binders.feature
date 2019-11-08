# language: no
@bidrag-dokument-journalpost
Egenskap:  bidrag-dokument-journalpost (/tilgang REST API)

  Tester tilgang URL

  Scenario: Sjekk at vi får en gyldig URL for dokument tilgang
    Gitt resttjenesten 'bidragDokumentJournalpost'
    Og jeg ber om tilgang til dokument på journalpostId 'jpId' og dokumentreferanse 'dokref'
    Så skal http status være '200'
    Og dokument url skal være gyldig
