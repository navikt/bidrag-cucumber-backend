# language: no
@bidrag-dokument-testdata
Egenskap: bidrag-dokument-testdata

  Tester REST API til endepunkt i bidrag-dokument-testdata.

  Scenario: Sjekk at health endpoint er operativt
    Gitt resttjenesten 'bidragDokumentTestdata'
    Når jeg kaller helsetjenesten
    Så skal http status være '200'
    Og responsen skal inneholde 'status' = 'UP'
