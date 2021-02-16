# language: no
@bidrag-dokument-journalpost
Egenskap: avvik bidrag-dokument-journalpost: endre fagomrade

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt resttjenesten 'bidragDokumentJournalpost' for avviksbehandling
    Og saksnummer '0000003' for avviksbehandling av 'ENDRE_FAGOMRADE'
    Og enhetsnummer for avvik er '4806'
    Og resttjenesten 'bidrag-testdata' for manipulering av testdata
    Og opprett journalpost når den ikke finnes for nøkkel:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test endre fagområde",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "journalstatus": "J",
        "gjelder": "29118044353",
        "journaldato": "2019-01-01",
        "mottattDato": "2019-01-01",
        "skannetDato": "2019-01-01",
        "saksnummer": "0000003"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være '200'
    Og listen med avvikstyper skal inneholde 'ENDRE_FAGOMRADE'

  @ignored
  Scenario: Sjekk at jeg kan endre fagområde til FAR
    Gitt detaljer 'fagomrade' = 'FAR'
    Når jeg oppretter avvik
    Så skal http status være '200'

  @ignored
  Scenario: Sjekk at endring av fagområde feiler når vi prøver å endre fra FAR til FAR
    Gitt detaljer 'fagomrade' = 'FAR'
    Når jeg oppretter avvik
    Så skal http status være '400'

  @ignored
  Scenario: Sjekk at jeg kan endre fagområde tilbake til BID
    Gitt detaljer 'fagomrade' = 'BID'
    Når jeg oppretter avvik
    Så skal http status være '200'

  @ignored
  Scenario: Sjekk at når man edrer fagområde til annet enn BID/FAR, så skal den være feilført
    Gitt detaljer 'fagomrade' = 'NYTT_FAGOMRADE'
    Når jeg oppretter avvik med bekreftelse at den er sendt scanning
    Så skal http status være '200'
    Og når jeg jeg henter journalpost etter avviksbehandling
    Så responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'feilfort' = 'true'
    Så responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'AF'
