# language: no
@bidrag-cucumber
Egenskap: bidrag-cucumber: sikkerhet
  Sjekker at oidc token fungerer for bidrag-ui

  Scenario: Sjekk at vi kan hente et id_token i 'q0'
    Gitt jeg bruker miljø: 'q0'
    Så kan vi hente id token

  Scenario: Sjekk at vi kan hente et id_token i 'q1'
    Gitt jeg bruker miljø: 'q1'
    Så kan vi hente id token

  Scenario: Sjekk at vi kan hente et id_token i 'q4'
    Gitt jeg bruker miljø: 'q4'
    Så kan vi hente id token
