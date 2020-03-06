# language: no
@dokument
Egenskap: bidrag-dokument (/tilgang REST API)

    Tester tilgang URL

    Scenario: Sjekk at vi får en gyldig URL for dokument tilgang for saksbehandler
        Gitt resttjenesten 'bidragDokument'
        Når jeg ber om tilgang til dokument på journalpostId '30040789' og dokumentreferanse 'abcdef'
        Så skal http status være '200'
        Og dokument url skal være gyldig
