# language: no
@bidrag-dokument-journalpost
Egenskap: avvik bidrag-dokument-journalpost: feilfore sak

  Tester REST API til journalpost endepunktet for avvik i bidrag-dokument.

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt resttjenesten bidragDokumentJournalpost for avviksbehandling
    Og saksnummer '0000003' for avviksbehandling av 'FEILFORE_SAK'
    Og enhetsnummeret '4806' til avviksbehandlingen
    Og opprett journalpost og ta vare på journalpostId:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test FEILFORE_SAK",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "29118044353",
        "journaldato": "2019-01-01",
        "journalforendeEnhet": "1289",
        "journalfortAv": "Behandler, Zakarias",
        "mottattDato": "2019-01-01",
        "saksnummer": "0000003"
        }
        """

#  Scenario: Sjekk avviksvalg for gitt journalpost
#    When jeg ber om gyldige avviksvalg for journalpost
#    Then statuskoden skal være '200'
#    And listen med valg skal inneholde 'FEILFORE_SAK'
##
##    Scenario: Sjekk at man kan feilfore sak
##        Given avvikstype 'FEILFORE_SAK'
##        When jeg kaller avvik endpoint
##        Then statuskoden skal være '200'
##
##    Scenario: Sjekk at avviksvalg for gitt journalpost ikke inneholder FEILFORE_SAK
##        When jeg ber om gyldige avviksvalg for journalpost
##        Then statuskoden skal være '200'
##		And listen med valg skal ikke inneholde 'FEILFORE_SAK'
##
##    Scenario: Sjekk at oppgave blir laget for bestill original
##        When jeg søker etter oppgaver for journalpost
##        Then statuskoden skal være '200'
#
