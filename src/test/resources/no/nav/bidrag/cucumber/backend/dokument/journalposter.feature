# language: no
@bidrag-dokument
Egenskap: bidrag-dokument (/sak/*/journal REST API)

  Tester REST API til journalpost endepunktet i bidrag-dokument.
  URLer til tjenester hentes via fasit.adeo.no og gjøres ved å spesifisere
  alias til en RestService record i fasit for et gitt miljø.

  Bakgrunn: Spesifiser base-url til tjenesten her så vi slipper å gjenta for hvert scenario.
  Fasit environment er gitt ved environment variabler ved oppstart.
    Gitt resttjenesten 'bidragDokument'

  Scenario: Sjekk operativt health endpoint
    Når jeg kaller helsetjenesten
    Så skal http status være '200'
    Og responsen skal inneholde 'status' = 'UP'

  @ignored
  Scenario: Sjekk at vi får en sakjournal for en sak
    Gitt jeg henter journalposter for sak "0000003" som har fagområde "BID"
    Så skal http status være '200'
    Og så skal responsen være en liste
    Og hvert element i listen skal ha følgende properties satt:
      | fagomrade    |
      | dokumenter   |
      | dokumentDato |

  @ignored
  Scenario: Sjekk at vi får korrekt basisinnhold journalpost for en gitt journalpostId
    Gitt jeg henter journalpost for sak "0000003" som har id "BID-19650256"
    Så skal http status være '200'
    Og så skal responsen være et objekt
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltene:
      | avsenderNavn  |
      | dokumentDato  |
      | dokumentType  |
      | journalstatus |
      | journalfortAv |
      | innhold       |

  Scenario: Sjekk at sak som ikke finnes git HttpStatus 404 - Not Found
    Gitt jeg henter journalpost for sak "XYZ" som har id "BID-12345667"
    Så skal http status være '404'

  @ignored
  Scenario: Sjekk at journalpost kan oppdateres - James Bond
    Gitt jeg endrer journalpost som har id 'BID-30040789':
            """
            {
            "journalpostId": 30040789,
            "saksnummer": {
            "erTilknyttetNySak": false,
            "saksnummer": "0000004",
            "saksnummerSomSkalErstattes":
            "0000004"
            },
            "gjelder": "***REMOVED***",
            "avsenderNavn": "Bond, James",
            "beskrivelse": "Søknad, Bidrag",
            "journaldato": "2006-05-09"
            }
            """
    Så skal http status være '200'
    Og jeg henter journalpost for sak "0000004" som har id "BID-30040789"
    Og skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'avsenderNavn' = 'Bond, James'

  @ignored
  Scenario: Sjekk at journalpost kan oppdateres - Trygdekontoret
    Gitt jeg endrer journalpost som har id 'BID-30040789':
            """
            {
            "journalpostId": 30040789,
            "saksnummer": {
            "erTilknyttetNySak": false,
            "saksnummer": "0000004",
            "saksnummerSomSkalErstattes":
            "0000004"
            },
            "gjelder": "***REMOVED***",
            "avsenderNavn": "Trygdekontoret",
            "beskrivelse": "Søknad, Bidrag",
            "journaldato": "2006-05-09"
            }
            """
    Så skal http status være '200'
    Og jeg henter journalpost for sak "0000004" som har id "BID-30040789"
    Og skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'avsenderNavn' = 'Trygdekontoret'

  @ignored
  Scenario: Sjekk at dokumentDato kan oppdateres til 2001-01-01
    Gitt jeg endrer journalpost som har id 'BID-30040789':
            """
            {
            "journalpostId": 30040789,
            "saksnummer": {
            "erTilknyttetNySak": false,
            "saksnummer": "0000004",
            "saksnummerSomSkalErstattes":
            "0000004"
            },
            "gjelder": "***REMOVED***",
            "avsenderNavn": "Bær, Bjarne",
            "beskrivelse": "Søknad, Bidrag",
            "journaldato": "2006-05-09",
            "dokumentDato": "2001-01-01"
            }
            """
    Så skal http status være '200'
    Og jeg henter journalpost for sak "0000004" som har id "BID-30040789"
    Og skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'dokumentDato' = '2001-01-01'

  @ignored
  Scenario: Sjekk at dokumentDato kan oppdateres til 2001-02-01
    Gitt jeg endrer journalpost som har id 'BID-30040789':
            """
            {
            "journalpostId": 30040789,
            "saksnummer": {
            "erTilknyttetNySak": false,
            "saksnummer": "0000004",
            "saksnummerSomSkalErstattes":
            "0000004"
            },
            "gjelder": "***REMOVED***",
            "avsenderNavn": "Bær, Bjarne",
            "beskrivelse": "Søknad, Bidrag",
            "journaldato": "2006-05-09",
            "dokumentDato": "2001-02-01"
            }
            """
    Så skal http status være '200'
    Og jeg henter journalpost for sak "0000004" som har id "BID-30040789"
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'dokumentDato' = '2001-02-01'
