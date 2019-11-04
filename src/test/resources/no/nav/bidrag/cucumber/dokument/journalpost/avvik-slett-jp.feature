# language: no
@bidrag-dokument-journalpost
Egenskap: avvik bidrag-dokument-journalpost: slett journalpost

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt resttjenesten bidragDokumentJournalpost for avviksbehandling
    Og saksnummer '0000003' for avviksbehandling av 'SLETT_JOURNALPOST'
    Og enhetsnummeret '4806' til avviksbehandlingen
    Og opprett journalpost og ta vare på journalpostId:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "batchNavn": "En batch",
        "beskrivelse": "Test slett journalpost",
        "dokumentType": "U",
        "fagomrade": "BID",
        "gjelder": "29118044353",
        "journaldato": "2019-01-01",
        "journalforendeEnhet": "1289",
        "journalfortAv": "Behandler, Zakarias",
        "journalstatus": "D",
        "mottattDato": "2019-01-01",
        "saksnummer": "0000003"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg med bidragDokumentJournalpost
    Så skal http status for avviksbehandlingen være '200'
    Og listen med avvikstyper skal inneholde 'SLETT_JOURNALPOST'

  Scenario: Sjekk at jeg kan slette journalpost
    Når jeg oppretter avvik med bidragDokumentJournalpost
    Så skal http status for avviksbehandlingen være '200'

  Scenario: Sjekk avviksvalg for gitt journalpost ikke inneholder slett journalpost
    Når jeg ber om gyldige avviksvalg med bidragDokumentJournalpost
    Så skal http status for avviksbehandlingen være '200'
    Og listen med avvikstyper skal ikke inneholde 'SLETT_JOURNALPOST'

#  Scenario: Sjekk at slettet journalpostid ikke lenger returneres i saksjournalen
#  When jeg henter journalposter for sak '0000003' med fagområde 'BID'
#  Then statuskoden skal være '200'
#  And listen med journalposter skal ikke inneholde journalpost.'journalpostId'
