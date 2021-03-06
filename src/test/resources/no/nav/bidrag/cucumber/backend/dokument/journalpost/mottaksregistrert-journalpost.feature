# language: no
@bidrag-dokument-journalpost
@mottaksregistrert
Egenskap: journalposter som har journalstatus mottaksregistrert i bidrag-dokument (/journal/* REST API)

  Tester REST API for journalposter som har journalstatus mottaksregistrert i bidrag-dokument.

  Bakgrunn: Tester hent journalpost uten sakstilknytning
    Gitt resttjenesten 'bidragDokumentJournalpost'
    Og resttjenesten 'bidrag-testdata' for manipulering av testdata
    Og lag bidragssak '0000004' når den ikke finnes fra før:
      """
        {
          "saksnummer": "0000004",
          "enhetsnummer": "4806"
        }
      """

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
        "mottattDato": "2020-02-02",
        "skannetDato": "2020-02-02",
        "journalstatus": "M"
        }
        """
    Og at jeg henter opprettet journalpost for 'MOTTAKSREGISTRERING_BDJ' med path '/journal/{}'
    Så skal http status være '200'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'M'

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
        "mottattDato": "2020-02-02",
        "skannetDato": "2020-02-02",
        "journalstatus": "J"
        }
        """
    Og at jeg henter opprettet journalpost for 'JOURNALFØRT_BDJ' med path '/journal/{}'
    Så skal http status være '200'
    Og responsen skal ikke inneholde 'journalstatus' = 'J'

  Scenario: Registrer (endre) journalpost som har status mottaksregistrert
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
    Så skal http status være '200'
    Og at jeg henter endret journalpost for 'REGISTRERING_BDJ' med path '/journal/{}'
    Så skal http status være '200'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'M'

  Scenario: Registrer (journalfør) journalpost som har status mottaksregistrert
    Gitt at jeg oppretter journalpost for 'JOURNALFOR_BDJ':
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
        "mottattDato": "2020-02-02",
        "skannetDato": "2020-02-02",
        "journalstatus": "M"
        }
        """
    Og jeg registrerer endring av opprettet journalpost, 'JOURNALFOR_BDJ', med path '/journal/{}', med enhet '4806':
      """
      {
        "skalJournalfores":true,
        "gjelder": "01117712345",
        "tittel":"journalfør",
        "tilknyttSaker":["0000004"],
        "endreDokumenter": [
          {
            "brevkode": "SVADA",
            "dokId": 0,
            "tittel": "journalfør"
          }
        ]
      }
      """
    Så skal http status være '200'
    Og at jeg henter endret journalpost for 'JOURNALFOR_BDJ' med path '/journal/{}'
    Så skal http status være '200'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'J'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalforendeEnhet' = '4806'
