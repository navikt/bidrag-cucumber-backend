# language: no
@bidrag-dokument-arkiv
Egenskap: bidrag-dokument-arkiv

  Tester REST API til endepunkt i bidrag-dokument-arkiv.
  URLer til tjenester hentes via fasit.adeo.no og gjøres ved å spesifisere
  alias til en RestService record i fasit for et gitt miljø.

  Bakgrunn: Spesifiser base-url til tjenesten her så vi slipper å gjenta for hvert scenario.
  Fasit environment er gitt ved environment variabler ved oppstart.
    Gitt resttjenesten 'bidragDokumentArkiv'

  Scenario: Sjekk at health endpoint er operativt
    Når jeg kaller helsetjenesten
    Så skal http status være '200'
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'

  @ignored
  Scenario: Sjekk at kall mot SAF er OK (ikke forventet å finne noe resultat) - ignorert grunnet sikkerhet mot saf feiler
    Når jeg kaller endpoint '/sak/1234567/journal' med parameter 'fagomrade' = 'BID'
    Så skal http status være '200'

#  Scenario: Opprett en journalpost og finn den via SAF query
#    Gitt at det opprettes en journalpost i joark med tema BID og saksnummer '1001001'
#    Når jeg kaller endpoint '/sak/1001001/journal' med parameter 'fagomrade' = 'BID'