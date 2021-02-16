# language: no
@bidrag-dokument-arkivering
Egenskap: bidrag-dokument-arkivering

  Tester REST API til endepunkt i bidrag-dokument-arkivering.

  Bakgrunn: Tester hent journalpost uten sakstilknytning
    Gitt nais applikasjon 'bidrag-dokument-arkivering' med kontekst ''
    Og resttjenesten 'bidrag-testdata' for manipulering av testdata

  @ignored
  Scenario: Arkivere reservert journalpost
    Gitt at jeg oppretter journalpost for 'ARKIVERING':
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test arkivere journalpost",
        "dokumentType": "U",
        "dokumentdato": "2020-08-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "123",
        "journaldato": "2020-08-01",
        "journalstatus"  : "R",
        "mottattDato": "2020-08-01",
        "skannetDato": "2020-08-01",
        "saksnummer": "0000003"
        }
        """
    Når jeg ber om at en journalpost uten dokument i brevlager, sendes til 'ARKIVERING' i Joark via bidrag-dokument-arkiveringsendepunkt med sti 'api/v1/arkivere/journalpost/{}'
    Så skal http status være '500'
#    Og responsen skal inneholde feilmeldingen 'Henting av dokument fra midlertidig brevlager feilet!'