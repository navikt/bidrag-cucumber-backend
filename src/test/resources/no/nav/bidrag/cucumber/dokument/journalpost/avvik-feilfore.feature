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
        "gjelder": "29118044353",
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
    Og listen med avvikstyper skal inneholde 'FEILFORE_SAK'

  Scenario: Sjekk at man kan feilfore sak
    Når jeg oppretter avvik
    Så skal http status være '200'

  Scenario: Sjekk at avviksvalg for gitt journalpost ikke inneholder FEILFORE_SAK
    Når jeg oppretter avvik
    Og når jeg jeg henter journalpost etter avviksbehandling
    Så skal http status være '200'
    Og responsen skal inneholde 'feilfort' = 'true'
    Og responsen skal ikke inneholde 'journalstatus' = 'S'
    Og når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være '200'
    Og listen med avvikstyper skal ikke inneholde 'FEILFORE_SAK'

  @feilfort
  Scenario: Sjekk at feilregistrert journalpost blir returnert i journalen og er feilført
    Når jeg oppretter avvik
    Og når jeg jeg henter sakjournalen etter avviksbehandling
    Så skal http status være '200'
    Og sakjournalen skal inneholde journalposten med felt 'feilfort' = 'true'
