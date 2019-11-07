# language: no
@bidrag-sak
Egenskap: bidrag-sak

  Tester REST API til endepunktet BidragSakController i bidrag-sak.
  URLer til tjenester hentes via fasit.adeo.no og gjøres ved å spesifisere
  alias til en RestService record i fasit for et gitt miljø.

  Scenario: Sjekk at health endpoint er operativt
    Gitt resttjenesten 'bidragSak' for sjekk av helsedata
    Når jeg kaller helsetjenesten
    Så skal http status for helsesjekken være '200'
    Og header 'content-type' skal være 'application/json;charset=UTF-8'
    Og helseresponsen skal inneholde 'status' = 'UP'

#  Scenario: Sjekk at vi får bidragssaker som involverer person angitt
#  When jeg henter bidragssaker for person med fnr "10099447805"
#  Then statuskoden skal være '200'
#  And hvert element i listen skal ha følgende properties satt:
#  | roller       |
#  | eierfogd     |
#  | saksstatus   |
#  | kategori     |
#  | erParagraf19 |
