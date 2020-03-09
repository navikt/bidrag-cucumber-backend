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

  Scenario: Sjekk at vi får bidragssaker som involverer person angitt
    Gitt resttjenesten 'bidragSak'
    Når jeg henter bidragssaker for person med fnr "10099447805"
    Så skal http status være '200'
    Og hvert element i listen skal ha følgende properties satt:
      | roller       |
      | eierfogd     |
      | saksstatus   |
      | kategori     |
      | erParagraf19 |

    @pip
    Scenario: Skal hente pip for sak 0000003
      Gitt resttjenesten 'bidragSak'
      Og bruk av en produksjonsbruker med tilgang til bidrag-sak pip
      Når jeg henter pip for sak '0000003'
      Så skal http status være '200'