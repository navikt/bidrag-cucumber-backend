# language: no
@bidrag-dokument-journalpost
Egenskap: avvik bidrag-dokument-journalpost: slett journalpost

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt resttjenesten 'bidragDokumentJournalpost' for avviksbehandling
    Og saksnummer '0000003' for avviksbehandling av 'SLETT_JOURNALPOST'
    Og enhetsnummer for avvik er '4806'
    Og resttjenesten 'bidrag-testdata' for manipulering av testdata
    Og opprett journalpost når den ikke finnes for nøkkel:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "batchNavn": "En batch",
        "beskrivelse": "Test slett journalpost",
        "dokumentType": "U",
        "journalstatus": "J",
        "fagomrade": "BID",
        "gjelder": "29118012345",
        "journaldato": "2019-01-01",
        "journalstatus": "D",
        "mottattDato": "2019-01-01",
        "saksnummer": "0000003"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være '200'
    Og listen med avvikstyper skal inneholde 'SLETT_JOURNALPOST'

  Scenario: Sjekk at jeg kan slette journalpost
    Når jeg oppretter avvik
    Så skal http status være '200'

  Scenario: Sjekk avviksvalg at for gitt journalpost med journalstatus slett journalpost skal være ei tom liste
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være '200'
    Og listen med avvikstyper skal være tom

  @ignored
  Scenario: Sjekk at slettet journalpostid ikke lenger returneres i saksjournalen
    Når jeg henter journalposter for sak '0000003' med fagområde 'BID' for å sjekke avviksbehandling
    Så skal http status være '200'
    Og listen med journalposter skal ikke inneholde id for journalposten
