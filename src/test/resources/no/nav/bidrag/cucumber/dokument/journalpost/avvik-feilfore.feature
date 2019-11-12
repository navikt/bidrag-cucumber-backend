# language: no
@bidrag-dokument-journalpost
Egenskap: avvik bidrag-dokument-journalpost: feilfore sak

  Tester REST API til journalpost endepunktet for avvik i bidrag-dokument.

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt resttjenesten 'bidragDokumentJournalpost' for avviksbehandling
    Og saksnummer '0000003' for avviksbehandling av 'FEILFORE_SAK'
    Og enhetsnummer for avvik er '4806'
    Og opprett journalpost og ta vare på journalpostId:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test FEILFORE_SAK",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "***REMOVED***",
        "journaldato": "2019-01-01",
        "journalforendeEnhet": "1289",
        "journalfortAv": "Behandler, Zakarias",
        "mottattDato": "2019-01-01",
        "saksnummer": "0000003"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være '200'
    Og listen med valg skal inneholde 'FEILFORE_SAK'

  Scenario: Sjekk at man kan feilfore sak
    Når jeg oppretter avvik
    Så skal http status være '200'

  Scenario: Sjekk at avviksvalg for gitt journalpost ikke inneholder FEILFORE_SAK
    Når jeg oppretter avvik
    Og jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være '200'
    Og når jeg jeg henter journalpost etter avviksbehandling
    Så skal http status være '200'
    Så skal responsen inneholde 'feilfort' = 'true'
    Og listen med valg skal ikke inneholde 'FEILFORE_SAK'
