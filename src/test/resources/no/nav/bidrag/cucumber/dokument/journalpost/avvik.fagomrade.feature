# language: no
@bidrag-dokument-journalpost
Egenskap: avvik bidrag-dokument-journalpost: endre fagomrade

  Bakgrunn: Opprett og cache journapost og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt resttjenesten bidragDokumentJournalpost for avviksbehandling
    Og saksnummer '0000003' for avviksbehandling av 'ENDRE_FAGOMRADE'
    Og opprettet, samt cachet journalpost:
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
#    And enhetsnummer '4806'
#
  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg med bidragDokumentJournalpost
    Så skal http status for avviksbehandlingen være '200'
#    And listen med valg skal inneholde 'ENDRE_FAGOMRADE'
#
#  Scenario: Sjekk at jeg kan endre fagområde til FAR
#    Given avvikstype 'ENDRE_FAGOMRADE'
#    And beskrivelse 'FAR'
#    When jeg kaller avvik endpoint
#    Then statuskoden skal være '200'
#
#  Scenario: Sjekk at endring av fagområde feiler når vi prøver å endre fra FAR til FAR
#    Given avvikstype 'ENDRE_FAGOMRADE'
#    And beskrivelse 'FAR'
#    When jeg kaller avvik endpoint
#    Then statuskoden skal være '400'
#
#  Scenario: Sjekk at jeg kan endre fagområde tilbake til BID
#    Given avvikstype 'ENDRE_FAGOMRADE'
#    And beskrivelse 'BID'
#    When jeg kaller avvik endpoint
#    Then statuskoden skal være '200'
#
