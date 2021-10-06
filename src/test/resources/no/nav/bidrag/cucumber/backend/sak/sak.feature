# language: no
@bidrag-sak
Egenskap: bidrag-sak

  Tester REST API til endepunktet BidragSakController i bidrag-sak.
  URLer til tjenester hentes via fasit.adeo.no og gjøres ved å spesifisere
  alias til en RestService record i fasit for et gitt miljø.

  Scenario: Sjekk at health endpoint er operativt
    Gitt resttjenesten 'bidragSak' for sjekk av helsedata
    Når jeg kaller helsetjenesten
    Så skal http status være '200'
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario: Sjekk at vi får NOT FOUND dersom vi ber om sak for person som ikke eksisterer i databasen
    Gitt resttjenesten 'bidragSak'
    Når jeg henter bidragssaker for person med fnr "12345678901"
    Så skal http status være '404'

  @ignored
  @pip
  Scenario: Skal gi 200 for sak 9999999
    Gitt resttjenesten 'bidragSak'
    Og bruk av en produksjonsbrukeren 'srvbisys' med tilgang til bidrag-sak pip
    Når jeg henter pip for sak '9999999'
    Så skal http status være '200'