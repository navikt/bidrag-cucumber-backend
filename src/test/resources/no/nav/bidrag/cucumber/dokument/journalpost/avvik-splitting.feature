# language: no
@bidrag-dokument-journalpost
Egenskap: avvik bidrag-dokument-journalpost: bestill splitting

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt resttjenesten 'bidragDokumentJournalpost' for avviksbehandling
    Og saksnummer '0000003' for avviksbehandling av 'BESTILL_SPLITTING'
    Og enhetsnummer for avvik er '4806'
    Og resttjenesten 'bidragDokumentTestdata' for manipulering av testdata
    Og opprett journalpost når den ikke finnes for nøkkel:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "batchNavn": "En batch",
        "beskrivelse": "Test bestill splitting",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "journalstatus": "J",
        "filnavn": "svada.pdf",
        "gjelder": "***REMOVED***",
        "journaldato": "2019-01-01",
        "mottattDato": "2019-01-01",
        "saksnummer": "0000003",
        "skannetDato": "2019-01-01",
        "filnavn": "svada.pdf"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være '200'
    Og listen med avvikstyper skal inneholde 'BESTILL_SPLITTING'

  Scenario: Sjekk at jeg kan bestille splitting
    Gitt beskrivelsen 'splitt midt på'
    Når jeg oppretter avvik
    Så skal http status være '201'
    Og når jeg jeg henter journalpost etter avviksbehandling
    Så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'feilfort' = 'true'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'S'

  Scenario: Sjekk at oppgave blir laget for splitting
    Gitt jeg søker etter oppgaver for journalpost
    Så skal http status for oppgavesøket være '200'
    Og søkeresultatet skal inneholde en oppgave
