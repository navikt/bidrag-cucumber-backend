# language: no
@bidrag-dokument-journalpost
Egenskap: avvik bidrag-dokument-journalpost: endre inn til utgående

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt resttjenesten bidragDokumentJournalpost for avviksbehandling
    Og saksnummer '0000003' for avviksbehandling av 'INNG_TIL_UTG_DOKUMENT'
    Og enhetsnummeret '4806' til avviksbehandlingen
    Og opprett journalpost og ta vare på journalpostId:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "batchNavn": "En batch",
        "beskrivelse": "Test inn til utgående",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "filnavn": "svada.pdf",
        "gjelder": "***REMOVED***",
        "journaldato": "2019-01-01",
        "journalforendeEnhet": "1289",
        "journalfortAv": "Behandler, Zakarias",
        "mottattDato": "2019-01-01",
        "saksnummer": "0000003",
        "skannetDato": "2019-01-01",
        "filnavn": "svada.pdf"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg med bidragDokumentJournalpost
    Så skal http status for avviksbehandlingen være '200'
    Og listen med avvikstyper skal inneholde 'INNG_TIL_UTG_DOKUMENT'

  Scenario: Sjekk at jeg kan opprette avvik inngående til utgående
    Når jeg oppretter avviket
    Så skal http status for avviksbehandlingen være '200'
