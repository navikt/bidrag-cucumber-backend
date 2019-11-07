# language: no
@bidrag-person
Egenskap: bidrag-person

  Bakgrunn: Felles egenskaper for alle scenario
    Gitt resttjenesten 'bidragPerson'

  Scenario: Sjekk at health endpoint er operativt
    Når jeg kaller helsetjenesten
    Så skal http status være '200'
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario: Sjekk at gyldig saksbehandler-id returnerer OK (200) respons
    Når jeg henter informasjon for ident '27067246654'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario: Sjekk at gyldig aktør-id returnerer OK (200) respons
    Når jeg henter informasjon for ident '1000065629588'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario: Sjekk at ugyldig person-id returnerer NO CONTENT (204) respons
    Når jeg henter informasjon for ident '27067299999'
    Og responsen skal inneholde 'status' = 'UP'
