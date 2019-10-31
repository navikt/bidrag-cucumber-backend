# language: no
@bidrag-dokument
Egenskap: bidrag-dokument (/sakjournal REST API)

  Tester REST API til bidrag dokument ui.
  URLer til tjenester hentes via fasit.adeo.no og gjøres ved å spesifisere
  alias til en RestService record i fasit for et gitt miljø.

  Bakgrunn: Spesifiser base-url til tjenesten her så vi slipper å gjenta for hvert scenario.
  Fasit environment er gitt ved environment variabler ved oppstart.
    Gitt resttjenesten bidragDokument til testing av sakjournal

  Scenario: Sjekk at vi får en liste med journalposter for en gitt sak
    Når jeg henter journalposter for sak "0000003" som har fagområde "BID" med bidragDokument
    Så skal journalresponsen.status være '200'
    Og så skal journalresponsen være en liste

  Scenario: Sjekk at vi får en journalpost for et farskap på gitt sak
    Når jeg henter journalposter for sak "0603479" som har fagområde "FAR" med bidragDokument
    Så skal journalresponsen.status være '200'
    Og så skal journalresponsen være en liste
    Og hver journal i listen skal ha 'fagomrade' = 'FAR'

  Scenario: Sjekk at vi får gjelderAktor i journalpost for et farskap på gitt sak
    Når jeg henter journalposter for sak "0603479" som har fagområde "FAR" med bidragDokument
    Så skal journalresponsen.status være '200'
    Og så skal journalresponsen være en liste
    Og hver journal i listen skal ha objektet 'gjelderAktor' med følgende properties:
      | ident |

  Scenario: Sjekk at saksnummer som ikke er heltall gir HttpStatus 400 (Bad Request)
    Når jeg henter journalposter for sak "XYZ" som har fagområde "FAR" med bidragDokument
    Så skal journalresponsen.status være '400'
