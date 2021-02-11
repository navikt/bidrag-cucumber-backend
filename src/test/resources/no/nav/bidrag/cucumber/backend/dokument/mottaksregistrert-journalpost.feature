# language: no
@bidrag-dokument
@mottaksregistrert
Egenskap: journalposter som har journalstatus mottaksregistrert i bidrag-dokument (/journal/* REST API)

  Tester REST API for journalposter som har journalstatus mottaksregistrert i bidrag-dokument.

  Bakgrunn: Tester hent journalpost uten sakstilknytning
    Gitt resttjenesten 'bidragDokument'
    Og resttjenesten 'bidrag-testdata' for manipulering av testdata

  Scenario: Hent med ugyldig prefix i journalpost id
    Gitt at jeg henter journalpost med path '/journal/XXX-123'
    Så skal http status være '400'

  Scenario: Hent med ukjent journalpost id
    Gitt at jeg henter journalpost med path '/journal/BID-123'
    Så skal http status være '404'

  Scenario: Hent en journalpost som har journalstatus mottaksregistrert
    Gitt at jeg oppretter journalpost for 'MOTTAKSREGISTRERING':
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
    Og at jeg henter opprettet journalpost for 'MOTTAKSREGISTRERING' med path '/journal/{}'
    Så skal http status være '200'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'M'

  Scenario: Hent en journalpost som ikke har journalstatus mottaksregistrert
    Gitt at jeg oppretter journalpost for 'JOURNALFØRT':
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
    Og at jeg henter opprettet journalpost for 'JOURNALFØRT' med path '/journal/{}'
    Så skal http status være '200'
    Og responsen skal ikke inneholde 'journalstatus' = 'J'

  Scenario: Registrer (endre) journalpost som har status mottaksregistrert
    Gitt at jeg oppretter journalpost for 'REGISTRERING':
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
    Og jeg registrerer endring av opprettet journalpost, 'REGISTRERING', med path '/journal/{}':
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
    Og at jeg henter endret journalpost for 'REGISTRERING' med path '/journal/{}'
    Så skal http status være '200'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'M'
    Og hendelsen 'REGISTRER_JOURNALPOST' skal være publisert for opprettet data med nokkel 'REGISTRERING'

  Scenario: Registrer (journalfør) journalpost som har status mottaksregistrert
    Gitt at jeg oppretter journalpost for 'JOURNALFOR':
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
    Og jeg registrerer endring av opprettet journalpost, 'JOURNALFOR', med path '/journal/{}':
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
    Så skal http status være '202'
    Og at jeg henter endret journalpost for 'JOURNALFOR' med path '/journal/{}'
    Så skal http status være '200'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'J'
    Og hendelsen 'REGISTRER_JOURNALPOST' skal være publisert for opprettet data med nokkel 'JOURNALFOR'

  Scenario: Registrer (journalfør) journalpost som har status mottaksregistrert
    Gitt at jeg oppretter journalpost for 'JOURNALFOR_DOK':
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
    Og jeg registrerer endring av opprettet journalpost, 'JOURNALFOR_DOK', med path '/journal/{}', med enhet '4806':
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
    Så skal http status være '202'
    Og at jeg henter endret journalpost for 'JOURNALFOR_DOK' med path '/journal/{}'
    Så skal http status være '200'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'J'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalforendeEnhet' = '4806'
