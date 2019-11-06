# language: no
@bidrag-dokument
Egenskap: bidrag-dokument (/sak/*/journal REST API)

  Tester REST API til journalpost endepunktet i bidrag-dokument.
  URLer til tjenester hentes via fasit.adeo.no og gjøres ved å spesifisere
  alias til en RestService record i fasit for et gitt miljø.

  Bakgrunn: Spesifiser base-url til tjenesten her så vi slipper å gjenta for hvert scenario.
  Fasit environment er gitt ved environment variabler ved oppstart.
    Gitt resttjenesten bidragDokument til testing av journalposter

  Scenario: Sjekk operativt health endpoint
    Gitt resttjenesten 'bidragDokumentJournalpost' for sjekk av helsedata
    Når jeg kaller helsetjenesten
    Så skal http status for helsesjekken være '200'
    Og helseresponsen skal inneholde 'status' = 'UP'

  Scenario: Sjekk at vi får en sakjournal for en sak
    Gitt jeg henter journalposter for sak "0000003" som har fagområde "BID" ned bidragDokument
    Så skal http status for testen være '200'
    Og så skal responsen være en liste
    Og hvert element i listen skal ha følgende properties satt:
      | fagomrade    |
      | dokumenter   |
      | dokumentDato |

  Scenario: Sjekk at vi får korrekt basisinnhold journalpost for en gitt journalpostId
    Gitt jeg henter journalpost for sak "0000003" som har id "BID-19650256" med bidragDokument
    Så skal http status for testen være '200'
    Og så skal responsen være et objekt
    Og følgende properties skal ligge i responsen:
      | avsenderNavn  |
      | dokumentDato  |
      | dokumentType  |
      | journalstatus |
      | journalfortAv |
      | innhold       |

  Scenario: Sjekk at sak uten tall gir HttpStatus 400 - Bad Request
    Gitt jeg henter journalpost for sak "XYZ" som har id "BID" med bidragDokument
    Så skal http status for testen være '400'

  Scenario: Sjekk at journalpost kan oppdateres - James Bond
    Gitt jeg endrer journalpost for sak '0000004' som har id 'BID-30040789' for bidragDokument:
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
    Så skal http status for testen være '202'
    Og jeg henter journalpost for sak "0000004" som har id "BID-30040789" med bidragDokument
    Og responsen skal inneholde 'avsenderNavn' = 'Bond, James'

  Scenario: Sjekk at journalpost kan oppdateres - Trygdekontoret
    Gitt jeg endrer journalpost for sak '0000004' som har id 'BID-30040789' for bidragDokument:
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
    Så skal http status for testen være '202'
    Og jeg henter journalpost for sak "0000004" som har id "BID-30040789" med bidragDokument
    Og responsen skal inneholde 'avsenderNavn' = 'Trygdekontoret'

  Scenario: Sjekk at dokumentDato kan oppdateres til 2001-01-01
    Gitt jeg endrer journalpost for sak '0000004' som har id 'BID-30040789' for bidragDokument:
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
    Så skal http status for testen være '202'
    Og jeg henter journalpost for sak "0000004" som har id "BID-30040789" med bidragDokument
    Og responsen skal inneholde 'dokumentDato' = '2001-01-01'

  Scenario: Sjekk at dokumentDato kan oppdateres til 2001-02-01
    Gitt jeg endrer journalpost for sak '0000004' som har id 'BID-30040789' for bidragDokument:
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
    Så skal http status for testen være '202'
    Og jeg henter journalpost for sak "0000004" som har id "BID-30040789" med bidragDokument
    Og responsen skal inneholde 'dokumentDato' = '2001-02-01'
