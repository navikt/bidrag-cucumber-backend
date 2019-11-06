# language: no
@bidrag-person
Egenskap: bidrag-person

  Scenario: Sjekk at health endpoint er operativt
    Gitt resttjenesten 'bidragPerson' for sjekk av helsedata
    Når jeg kaller helsetjenesten
    Så skal http status for helsesjekken være '200'
    Og header 'content-type' skal være 'application/json;charset=UTF-8'
    Og helseresponsen skal inneholde 'status' = 'UP'

  Scenario: Sjekk at gyldig saksbehandler-id returnerer OK (200) respons
    Gitt resttjenesten bidragPerson
    Når jeg henter informasjon for ident '27067246654'
    Så skal http status fra bidragPerson være '200'

#  Scenario: Sjekk at gyldig aktør-id returnerer OK (200) respons
#    When jeg henter informasjon for ident '1000065629588'
#    Then statuskoden skal være '200'
#
#  Scenario: Sjekk at ugyldig person-id returnerer NO CONTENT (204) respons
#    When jeg henter informasjon for ident '27067299999'
#    Then statuskoden skal være '204'