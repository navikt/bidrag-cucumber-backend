# language: no
@bidrag-dokument
@avviksbehandling
Egenskap: avvik for bidrag-dokument (/journal/*/avvik REST API)

  Tester REST API for avvik i bidrag-dokument.

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt resttjenesten 'bidragDokument' for avviksbehandling
    Og saksnummer '0000003' for avviksbehandling for nøkkel 'TEST_AVVIKSBEHANDLING'
    Og enhetsnummer for avvik er '4806'
    Og resttjenesten 'bidrag-testdata' for manipulering av testdata
    Og opprett journalpost når den ikke finnes for nøkkel:
            """
            {
                "batchNavn": "batchen",
                "beskrivelse": "Test avviksbehandling",
                "fagomrade": "BID",
                "feilfortSak": "false",
                "filnavn": "fila",
                "dokumentType": "I",
                "journalstatus": "J",
                "originalBestilt": "false",
                "saksnummer": "0000003",
                "skannetDato": "2019-08-21"
            }
            """

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg for opprettet journalpost med nøkkel 'TEST_AVVIKSBEHANDLING'
    Så skal http status være '200'
    Og listen med avvikstyper skal kun inneholde:
      | BESTILL_ORIGINAL      |
      | BESTILL_RESKANNING    |
      | BESTILL_SPLITTING     |
      | ENDRE_FAGOMRADE       |
      | INNG_TIL_UTG_DOKUMENT |
      | FEILFORE_SAK          |

  Scenario: Sjekk at man kan bestille original
    Gitt avvikstype 'BESTILL_ORIGINAL'
    Og detaljer 'enhetsnummer' = '4806'
    Når jeg oppretter avvik på opprettet journalpost med nøkkel 'TEST_AVVIKSBEHANDLING'
    Så skal http status være '200'

  Scenario: Sjekk at avviksvalg for gitt journalpost ikke inneholder BESTILL_ORIGINAL
    Gitt avvikstype 'BESTILL_ORIGINAL'
    Og detaljer 'enhetsnummer' = '4806'
    Når jeg oppretter avvik på opprettet journalpost med nøkkel 'TEST_AVVIKSBEHANDLING'
    Og jeg ber om gyldige avviksvalg for journalpost
    Så skal http status være '200'
    Og listen med avvikstyper skal ikke inneholde 'BESTILL_ORIGINAL'

  Scenario: Sjekk at man kan bestille reskannning
    Gitt avvikstype 'BESTILL_RESKANNING'
    Når jeg oppretter avvik på opprettet journalpost med nøkkel 'TEST_AVVIKSBEHANDLING'
    Så skal http status være '200'

  Scenario: Sjekk at man ikke kan bestille ukjent avvik
    Gitt avvikstype 'BLAH_BLAH_LAH_123'
    Når jeg oppretter avvik på opprettet journalpost med nøkkel 'TEST_AVVIKSBEHANDLING'
    Så skal http status være '400'

  Scenario: Sjekk at man kan bestille splitting
    Gitt avvikstype 'BESTILL_SPLITTING'
    Og avvikstypen har beskrivelse 'Splitt på midten'
    Når jeg oppretter avvik på opprettet journalpost med nøkkel 'TEST_AVVIKSBEHANDLING'
    Så skal http status være '200'

  Scenario: Sjekk at man kan endre fagområde til FAR
    Gitt avvikstype 'ENDRE_FAGOMRADE'
    Og detaljer 'fagomrade' = 'FAR'
    Og detaljer 'enhetsnummer' = '4806'
    Når jeg oppretter avvik på opprettet journalpost med nøkkel 'TEST_AVVIKSBEHANDLING'
    Så skal http status være '200'

  Scenario: Sjekk at endring av fagområde feiler når vi prøver å endre fra FAR til FAR
    Gitt avvikstype 'ENDRE_FAGOMRADE'
    Og detaljer 'fagomrade' = 'FAR'
    Og detaljer 'enhetsnummer' = '4806'
    Når jeg oppretter avvik på opprettet journalpost med nøkkel 'TEST_AVVIKSBEHANDLING'
    Når jeg oppretter avvik på opprettet journalpost med nøkkel 'TEST_AVVIKSBEHANDLING'
    Så skal http status være '400'
