# language: no
@bidrag-beregn-barnebidrag-rest
Egenskap: bidrag-beregn-barnebidrag-rest

  Tester REST API til endepunkt i bidrag-beregn-barnebidrag-rest.
  URLer til tjenester hentes via nais/nais.yaml og gjøres ved å navngi prosjektet som man skal
  kommunisere med (via REST).

  Bakgrunn: Rest-tjeneste.
    Gitt nais applikasjon 'bidrag-beregn-barnebidrag-rest'

  Scenario: Sjekk at health endpoint er operativt
    Når jeg kaller helsetjenesten
    Så skal http status være '200'
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario:
    Når jeg bruker endpoint '/beregn/barnebidrag' med json fra 'src/test/resources/no/nav/bidrag/cucumber/beregn/barnebidrag/barnebidrag_eksempel1.json'
    Så skal http status være '200'
    Og responsen skal inneholde beløpet 3490 under stien '$.beregnBarnebidragResultat.resultatPeriodeListe[0].resultatBeregningListe[0].resultatBelop'
    Og responsen skal inneholde en streng 'KOSTNADSBEREGNET_BIDRAG' under stien '$.beregnBarnebidragResultat.resultatPeriodeListe[0].resultatBeregningListe[0].resultatKode'
