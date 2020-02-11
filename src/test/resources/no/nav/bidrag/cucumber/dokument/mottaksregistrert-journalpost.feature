# language: no
@bidrag-dokument
@mottaksregistrert-journalpost
Egenskap: journalposter som har journalstatus mottaksregistrert i bidrag-dokument (/journal/* REST API)

  Tester REST API for journalposter som har journalstatus mottaksregistrert i bidrag-dokument.

  Bakgrunn: Tester hent journalpost uten sakstilknytning
    Gitt resttjenesten 'bidragDokument'
    Og resttjenesten 'bidragDokumentTestdata' for manipulering av testdata
    Og opprett journalpost når den ikke finnes for 'MOTTAKSREGISTRERING':
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test journalpost med journalstatus 'M'",
        "dokumentType": "I",
        "dokumentdato": "2020-02-02",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "06127412345",
        "journaldato": "2020-02-02",
        "journalforendeEnhet": "1289",
        "journalfortAv": "Behandler, Zakarias",
        "mottattDato": "2020-02-02",
        "skannetDato": "2020-02-02",
        "journalstatus": "M"
        }
        """

  Scenario: Hent med ugyldig prefix i journalpost id
    Gitt at jeg henter journalpost med path '/journal/XXX-123'
    Så skal http status være '400'

  Scenario: Hent med ukjent journalpost id
    Gitt at jeg henter journalpost med path '/journal/BID-123'
    Så skal http status være '404'

  Scenario: Hent en journalpost som har journalstatus mottaksregistrert
    Gitt at jeg henter opprettet journalpost for 'MOTTAKSREGISTRERING' med http-api '/journal/{}'
    Så skal http status være '200'
    Og responsen skal inneholde 'journalstatus' = 'M'
