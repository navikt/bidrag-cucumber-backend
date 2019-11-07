# language: no
@bidrag-dokument-journalpost
Egenskap: bidrag-dokument-journalpost (/sakjournal REST API)

  Bakgrunn: Spesifiser base-url til tjenesten her så vi slipper å gjenta for hvert scenario.
  Fasit environment er gitt ved environment variabler ved oppstart.
    Gitt resttjenesten 'bidragDokumentJournalpost'

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
