# language: no
@bidrag-cucumber
Egenskap: bidrag-cucumber: sikkerhet
  Sjekker at oidc token fungerer for bidrag-ui

  Scenario: Sjekk at vi kan hente et id_token i 'q0'
    Gitt jeg bruker miljø: 'q0'
    Så kommer det ikke noen exception ved henting av id token

  Scenario: Sjekk at vi kan hente et id_token i 'q1'
    Gitt jeg bruker miljø: 'q1'
    Så kommer det ikke noen exception ved henting av id token

  Scenario: Sjekk at vi kan hente et id_token i 'q4'
    Gitt jeg bruker miljø: 'q4'
    Så kommer det ikke noen exception ved henting av id token
