# language: no
@bidrag-dokument-journalpost
@sakjournal
Egenskap: bidrag-dokument-journalpost (/sak/(saksnummer)/journal REST API)

  Bakgrunn: Spesifiser base-url til tjenesten her så vi slipper å gjenta for hvert scenario.
  Fasit environment er gitt ved environment variabler ved oppstart.
    Gitt resttjenesten 'bidragDokumentJournalpost'
    Og resttjenesten 'bidrag-testdata' for manipulering av testdata
    Og opprett journalpost med nøkkel 'SAK_BDJ' når den ikke finnes:
      """
        {
          "avsenderNavn": "Cucumber Test",
          "beskrivelse": "Test endre fagområde",
          "dokumentType": "I",
          "dokumentdato": "2019-01-01",
          "dokumentreferanse": "1234567890",
          "fagomrade": "FAR",
          "gjelder": "29118012345",
          "journalforendeEnhet": "4802",
          "journaldato": "2019-01-01",
          "journalstatus": "J",
          "mottattDato": "2019-01-01",
          "saksnummer": "0603479",
          "skannetDato": "2019-01-01"
        }
      """

  Scenario: Sjekk at vi får en liste med journalposter for en gitt sak
    Når jeg henter journalposter for sak "0000003" som har fagområde "BID"
    Så skal http status være '200'
    Og så skal responsen være en liste

  Scenario: Sjekk at vi får en journalpost for et farskap på gitt sak
    Når jeg henter journalposter for sak "0603479" som har fagområde "FAR"
    Så skal http status være '200'
    Og så skal responsen være en liste
    Og hver journal i listen skal ha 'fagomrade' = 'FAR'

  Scenario: Sjekk at vi får gjelderAktor i journalpost for et farskap på gitt sak
    Når jeg henter journalposter for sak "0603479" som har fagområde "FAR"
    Så skal http status være '200'
    Og så skal responsen være en liste
    Og hver journal i listen skal ha objektet 'gjelderAktor' med følgende properties:
      | ident |
