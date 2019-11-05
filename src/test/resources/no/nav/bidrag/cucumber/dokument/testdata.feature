# language: no
@bidrag-dokument-testdata
Egenskap: bidrag-dokument-testdata

  Tester REST API til endepunkt i bidrag-dokument-testdata.

  Scenario: Sjekk at health endpoint er operativt
    Gitt resttjenesten 'bidragDokumentTestdata' for sjekk av helsedata
    Når jeg sjekker resttjenestens status
    Så skal http status for helsesjekken være '200'
    Og helseresponsen skal inneholde 'status' = 'UP'
