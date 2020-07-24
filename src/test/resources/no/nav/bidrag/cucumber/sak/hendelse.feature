# language: no
@bidrag-sak
Egenskap: bidrag-sak

  Tester REST API til endepunktet BidragHendesleController i bidrag-sak.
  URLer til tjenester hentes via fasit.adeo.no og gjøres ved å spesifisere
  alias til en RestService record i fasit for et gitt miljø.

  Scenario: Sjekk at vi får bidraghendelser for en sak
    Gitt resttjenesten 'bidragSak'
    Når jeg henter bidraghendelser for sak med saksnummer "1500000"
    Så skal http status være '200'
