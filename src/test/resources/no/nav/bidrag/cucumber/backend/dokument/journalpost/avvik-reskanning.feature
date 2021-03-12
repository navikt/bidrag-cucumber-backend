# language: no
@bidrag-dokument-journalpost
Egenskap: avvik bidrag-dokument-journalpost: reskanning

  Tester REST API til journalpost endepunktet for avvik i bidrag-dokument.

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt resttjenesten 'bidragDokumentJournalpost' for avviksbehandling
    Og saksnummer '0000003' for avviksbehandling av 'BESTILL_RESKANNING'
    Og enhetsnummer for avvik er '4806'
    Og resttjenesten 'bidrag-testdata' for manipulering av testdata
    Og opprett journalpost når den ikke finnes for nøkkel:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test reskanning",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "string",
        "fagomrade": "BID",
        "journalstatus": "J",
        "gjelder": "29118044353",
        "journaldato": "2019-01-01",
        "mottattDato": "2019-01-01",
        "saksnummer": "0000003",
        "skannetDato": "2019-01-01"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost inneholder BESTILL_RESKANNING
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være '200'
    Og listen med avvikstyper skal inneholde 'BESTILL_RESKANNING'

  @ignored
  Scenario: Sjekk at reskanning kan bestilles
    Når jeg oppretter avvik
    Så skal http status være '200'

  @ignored
  Scenario: Sjekk at oppgave blir laget for reskanning
    Gitt jeg søker etter oppgaver for journalpost
    Så skal http status for oppgavesøket være '200'
    Og søkeresultatet skal inneholde en oppgave

  @ignored
  Scenario: Sjekk at når man bestiller reskanning, så skal journalposten bli feilført
    Når jeg oppretter avvik
    Så skal http status være '200'
    Og når jeg jeg henter journalpost etter avviksbehandling
    Så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'feilfort' = 'true'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'AR'
