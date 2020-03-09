# language: no
@bidrag-dokument
Egenskap: avvik for bidrag-dokument (/sak/*/journal/*/avvik REST API)

  Tester REST API for avvik i bidrag-dokument.

  Bakgrunn: Tester rest for bidragDokument og bruker resttjeneste bidragDokumentTestdata for å manipulere databasen før test
    Gitt resttjenesten 'bidragDokument' for avviksbehandling
    Og resttjenesten 'bidragDokumentTestdata' for manipulering av testdata
    Og endre journalpost med id '34111047' til:
            """
            {
                "fagomrade": "BID",
                "feilfort": "false",
                "dokumentType": "I",
                "originalBestilt": "false",
                "skannetDato": "2019-08-21"
            }
            """
    Og endepunkt url lages av saksnummer '0000003' og journalpostId 'BID-34111047'
    Og enhetsnummer for avvik er '4806'

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg for journalpost
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
    Når jeg oppretter avvik
    Så skal http status være '201'

  Scenario: Sjekk at avviksvalg for gitt journalpost ikke inneholder BESTILL_ORIGINAL
    Gitt avvikstype 'BESTILL_ORIGINAL'
    Når jeg oppretter avvik
    Og jeg ber om gyldige avviksvalg for journalpost
    Så skal http status være '200'
    Og listen med avvikstyper skal ikke inneholde 'BESTILL_ORIGINAL'

  Scenario: Sjekk at man kan bestille reskannning
    Gitt avvikstype 'BESTILL_RESKANNING'
    Når jeg oppretter avvik
    Så skal http status være '201'

  Scenario: Sjekk at man ikke kan bestille ukjent avvik
    Gitt avvikstype 'BLAH_BLAH_LAH_123'
    Når jeg oppretter avvik
    Så skal http status være '400'

  Scenario: Sjekk at man kan bestille splitting
    Gitt avvikstype 'BESTILL_SPLITTING'
    Og avvikstypen har beskrivelse 'Splitt på midten'
    Når jeg oppretter avvik
    Så skal http status være '201'

  Scenario: Sjekk at man kan endre fagområde til FAR
    Gitt avvikstype 'ENDRE_FAGOMRADE'
    Og avvikstypen har beskrivelse 'FAR'
    Når jeg oppretter avvik
    Så skal http status være '200'

  Scenario: Sjekk at endring av fagområde feiler når vi prøver å endre fra FAR til FAR
    Gitt avvikstype 'ENDRE_FAGOMRADE'
    Og avvikstypen har beskrivelse 'FAR'
    Når jeg oppretter avvik
    Og jeg oppretter avvik
    Så skal http status være '400'

  Scenario: Sjekk at man kan feilføre sak
    Gitt avvikstype 'FEILFORE_SAK'
    Når jeg oppretter avvik
    Så skal http status være '200'
