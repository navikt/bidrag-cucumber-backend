# language: no
@bidrag-cucumber
Egenskap: bidrag-cucumber: environment
  Sjekker at oidc token fungerer for bidrag-dokument-ui

  Scenario: Sjekk at vi får et gyldig id_token i 'q0'
    Gitt jeg bruker miljø: 'q0'
    Når man sjekker at token er gyldig
    Så skal token ha følgende properties:
      | aud       | bidrag-dokument-ui-q0 |
      | sub       | bidrag-dokument-ui-q0 |
      | tokenType | JWTToken              |

  Scenario: Sjekk at vi får et gyldig id_token i 'q0' for testbruker
    Gitt jeg bruker miljø: 'q0'
    Når man sjekker at bruker-token er gyldig
    Så skal token ha følgende properties:
      | aud       | bidrag-dokument-ui-q0  |
      | sub       | $process.env.test_user |
      | tokenType | JWTToken               |

  Scenario: Sjekk at vi får et gyldig id_token i 'q1'
    Gitt jeg bruker miljø: 'q1'
    Når man sjekker at token er gyldig
    Så skal token ha følgende properties:
      | aud       | bidrag-dokument-ui-q1 |
      | sub       | bidrag-dokument-ui-q1 |
      | tokenType | JWTToken              |

  Scenario: Sjekk at vi får et gyldig id_token i 'q4'
    Gitt jeg bruker miljø: 'q4'
    Når man sjekker at token er gyldig
    Så skal token ha følgende properties:
      | aud       | bidrag-dokument-ui-q4 |
      | sub       | bidrag-dokument-ui-q4 |
      | tokenType | JWTToken              |
