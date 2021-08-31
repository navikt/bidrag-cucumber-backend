# language: no
@bidrag-beregn-saertilskudd-rest
Egenskap: bidrag-beregn-saertilskudd-rest

  Tester REST API til endepunkt i bidrag-beregn-saertilskudd-rest.
  URLer til tjenester hentes via nais/nais.yaml og gjøres ved å navngi prosjektet som man skal
  kommunisere med (via REST).

  Bakgrunn: Rest-tjeneste.
    Gitt nais applikasjon 'bidrag-beregn-saertilskudd-rest'

  Scenario: Sjekk at health endpoint er operativt
    Når jeg kaller helsetjenesten
    Så skal http status være '200'
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario:
    Når jeg bruker endpoint '/beregn/saertilskudd' med json fra 'saertilskudd/saertilskudd_eksempel1.json'
    Så skal http status være '200'
    Og responsen skal inneholde beløpet '4242' under stien '$.beregnetSaertilskuddPeriodeListe[0].resultat.belop'
    Og responsen skal inneholde resultatkoden 'SAERTILSKUDD_INNVILGET' under stien '$.beregnetSaertilskuddPeriodeListe[0].resultat.kode'
