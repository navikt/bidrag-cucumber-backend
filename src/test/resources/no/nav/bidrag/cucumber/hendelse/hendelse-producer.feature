# language: no
@bidrag-hendelse-producer
Egenskap: bidrag-hendelse-producer REST API

  Bakgrunn: Test av
    Gitt nais applikasjon 'bidrag-hendelse-producer'

 Scenario: Sjekk at health endpoint er operativt
    Når jeg kaller helsetjenesten
    Så skal http status være '200'
    Og responsen skal inneholde 'status' = 'UP'

