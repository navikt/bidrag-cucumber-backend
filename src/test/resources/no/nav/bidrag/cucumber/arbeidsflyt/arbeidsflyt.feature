# language: no
@bidrag-arbeidsflyt
Egenskap: bidrag-arbeidsflyt

  Tester REST API til endepunkt i bidrag-arbeidsflyt.

  Scenario: Sjekk at health endpoint er operativt
    Gitt nais applikasjon 'bidrag-arbeidsflyt'
    Når jeg kaller helsetjenesten
    Så skal http status være '200'
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'
