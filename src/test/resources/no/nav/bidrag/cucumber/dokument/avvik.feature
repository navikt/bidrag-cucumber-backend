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

  Scenario: Sjekk at man kan bestille original
  Gitt avvikstype 'BESTILL_ORIGINAL'
  Når jeg oppretter avvik
  Så skal http status for avvik være '201'
