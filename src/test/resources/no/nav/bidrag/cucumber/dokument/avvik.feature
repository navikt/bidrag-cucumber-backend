# language: no
@bidrag-dokument
Egenskap: avvik for bidrag-dokument (/sak/*/journal/*/avvik REST API)

  Tester REST API for avvik i bidrag-dokument.

  Bakgrunn: Tester rest for bidragDokument og bruker resttjeneste bidragDokumentTestdata for å manipulere databasen før test
    Gitt data på journalpost med id '34111047' inneholder:
            """
            {
                "fagomrade": "BID",
                "feilfort": "false",
                "dokumentType": "I",
                "originalBestilt": "false",
                "skannetDato": "2019-08-21"
            }
            """
    Og endepunkt url er '/sak/0000003/journal/BID-34111047/avvik'
    Og enhetsnummer for avvik er '4806'

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg for journalpost
    Så skal http status for avvik være '200'
    Og listen med valg skal kun inneholde:
      | BESTILL_ORIGINAL      |
      | BESTILL_RESKANNING    |
      | BESTILL_SPLITTING     |
      | ENDRE_FAGOMRADE       |
      | INNG_TIL_UTG_DOKUMENT |
      | FEILFORE_SAK          |

  Scenario: Sjekk at man kan bestille original
    Gitt avvikstype 'BESTILL_ORIGINAL'
    Når jeg oppretter avvik
    Så skal http status for avvik være '201'

  Scenario: Sjekk at avviksvalg for gitt journalpost ikke inneholder BESTILL_ORIGINAL
    Gitt avvikstype 'BESTILL_ORIGINAL'
    Når jeg oppretter avvik
    Og jeg ber om gyldige avviksvalg for journalpost
    Så skal http status for avvik være '200'
    Og listen med valg skal ikke inneholde 'BESTILL_ORIGINAL'

  Scenario: Sjekk at kan bestille reskannning
    Gitt avvikstype 'BESTILL_RESKANNING'
    Når jeg oppretter avvik
    Så skal http status for avvik være '201'