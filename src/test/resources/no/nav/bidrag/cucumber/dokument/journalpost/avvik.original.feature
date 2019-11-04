# language: no
@bidrag-dokument-journalpost
Egenskap: avvik bidrag-dokument-journalpost: bestill original

  Tester REST API til journalpost endepunktet for avvik i bidrag-dokument.

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt resttjenesten bidragDokumentJournalpost for avviksbehandling
    Og saksnummer '0000003' for avviksbehandling av 'BESTILL_ORIGINAL'
    Og enhetsnummeret '4806' til avviksbehandlingen
    Og opprett journalpost og ta vare på journalpostId:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test bestill original",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "***REMOVED***",
        "journaldato": "2019-01-01",
        "journalforendeEnhet": "1289",
        "journalfortAv": "Behandler, Zakarias",
        "mottattDato": "2019-01-01",
        "originalBestilt": "false",
        "saksnummer": "0000003",
        "skannetDato": "2019-01-01"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost inneholder BESTILL_ORIGINAL
    Når jeg ber om gyldige avviksvalg med bidragDokumentJournalpost
    Så skal http status for avviksbehandlingen være '200'
    Og listen med avvikstyper skal inneholde 'BESTILL_ORIGINAL'

  Scenario: Sjekk at man kan bestille original
    Når jeg oppretter avviket
    Så skal http status for avviksbehandlingen være '201'

  Scenario: Sjekk at avviksvalg for gitt journalpost ikke inneholder BESTILL_ORIGINAL
    Når jeg ber om gyldige avviksvalg med bidragDokumentJournalpost
    Så skal http status for avviksbehandlingen være '200'
    Og listen med avvikstyper skal ikke inneholde 'BESTILL_ORIGINAL'

  Scenario: Sjekk at oppgave blir laget for bestill original
    Gitt jeg søker etter oppgaver for journalpost
    Så skal http status for oppgavesøket være '200'
# todo fix:    Og søkeresultatet skal inneholde en oppgave
