# language: no
@bidrag-dokument-journalpost
Egenskap: avvik bidrag-dokument-journalpost: endre fagomrade

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt resttjenesten bidragDokumentJournalpost for avviksbehandling
    Og saksnummer '0000003' for avviksbehandling av 'ENDRE_FAGOMRADE'
    Og enhetsnummeret '4806' til avviksbehandlingen
    Og opprett journalpost og ta vare på journalpostId:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test endre fagområde",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "***REMOVED***",
        "journaldato": "2019-01-01",
        "journalforendeEnhet": "1289",
        "journalfortAv": "Behandler, Zakarias",
        "mottattDato": "2019-01-01",
        "skannetDato": "2019-01-01",
        "saksnummer": "0000003"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg med bidragDokumentJournalpost
    Så skal http status for avviksbehandlingen være '200'
    Og listen med avvikstyper skal inneholde 'ENDRE_FAGOMRADE'

  Scenario: Sjekk at jeg kan endre fagområde til FAR
    Gitt beskrivelsen 'FAR'
    Når jeg oppretter avviket
    Så skal http status for avviksbehandlingen være '200'

  Scenario: Sjekk at endring av fagområde feiler når vi prøver å endre fra BID til BID
    Gitt beskrivelsen 'BID'
    Når jeg oppretter avviket
    Så skal http status for avviksbehandlingen være '400'

  Scenario: Sjekk at jeg kan endre fagområde tilbake til BID
    Gitt beskrivelsen 'FAR'
    Når jeg oppretter avviket
    Og beskrivelsen 'BID'
    Når jeg oppretter avviket
    Så skal http status for avviksbehandlingen være '200'
