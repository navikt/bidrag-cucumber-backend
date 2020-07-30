# language: no
@bidrag-hendelse
Egenskap: bidrag-hendelse

  Tester REST API til endepunktet BidragHendesleController i bidrag-hendelse.
  URLer til tjenester hentes via fasit.adeo.no og gjøres ved å spesifisere
  alias til en RestService record i fasit for et gitt miljø.

  Bakgrunn: Test av
    Gitt nais applikasjon 'bidrag-hendelse'

  Scenario: Sjekk at vi får bidraghendelser for en sak
    Når jeg henter bidraghendelser for sak med saksnummer "1500000"
    Så skal http status være '200'
    Og så skal responsen være en liste
    Og hvert element i listen skal ha følgende properties satt:
      | hendelsetype  |
      | enhet         |
      | opprettetDato |
      | resultat      |
