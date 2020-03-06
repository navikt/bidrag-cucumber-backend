# language: no
@bidrag-dokument-journalpost
@mottaksregistrert-journalpost
Egenskap: journalposter som har journalstatus mottaksregistrert i bidrag-dokument (/journal/* REST API)

  Tester REST API for journalposter som har journalstatus mottaksregistrert i bidrag-dokument.

  Bakgrunn: Tester hent journalpost uten sakstilknytning
    Gitt resttjenesten 'bidragDokumentJournalpost'
    Og resttjenesten 'bidragDokumentTestdata' for manipulering av testdata

  Scenario: Hent med ugyldig prefix i journalpost id
    Gitt at jeg henter journalpost med path '/journal/XXX-123'
    Så skal http status være '400'

  Scenario: Hent med ukjent journalpost id
    Gitt at jeg henter journalpost med path '/journal/BID-123'
    Så skal http status være '404'

  Scenario: Hent en journalpost som har journalstatus mottaksregistrert
    Gitt at jeg oppretter journalpost for 'MOTTAKSREGISTRERING_BDJ':
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Testdata for test av journalpost med journalstatus 'M'",
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
    Og at jeg henter opprettet journalpost for 'MOTTAKSREGISTRERING_BDJ' med path '/journal/{}'
    Så skal http status være '200'
    Og responsen skal inneholde 'journalstatus' = 'M'

  Scenario: Hent en journalpost som ikke har journalstatus mottaksregistrert
    Gitt at jeg oppretter journalpost for 'JOURNALFØRT_BDJ':
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Testdata for test av journalpost med journalstatus 'M'",
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
        "journalstatus": "J"
        }
        """
    Og at jeg henter opprettet journalpost for 'JOURNALFØRT_BDJ' med path '/journal/{}'
    Så skal http status være '204'

  Scenario: Registrer (journalfør) journalpost som har status mottaksregistrert
    Gitt at jeg oppretter journalpost for 'REGISTRERING_BDJ':
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Testdata for test av journalpost med journalstatus 'M'",
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
    Og jeg registrerer endring av opprettet journalpost, 'REGISTRERING_BDJ', med path '/journal/{}':
      """
      {
        "skalJournalfores":false,
        "gjelder": "01117712345",
        "tittel":"journalfør",
        "endreDokumenter": [
          {
            "brevkode": "SVADA",
            "dokId": 0,
            "tittel": "journalfør"
          }
        ]
      }
      """
    Så skal http status være '202'
    Og at jeg henter endret journalpost for 'REGISTRERING_BDJ' med path '/journal/{}'
    Så skal http status være '200'
    Og responsen skal inneholde 'journalstatus' = 'M'
