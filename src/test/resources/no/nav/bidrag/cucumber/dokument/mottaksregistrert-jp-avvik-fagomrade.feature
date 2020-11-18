# language: no
@bidrag-dokument
@mottaksregistrert
Egenskap: Avvikshendelse ENDRE_FAGOMRADE på journalposter som er mottaksregistrert (/journal/*/avvik REST API)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt resttjenesten 'bidragDokument' for å registrere avvik på mottaksredigert journalpost, avvikstype 'ENDRE_FAGOMRADE'
    Og resttjenesten 'bidragDokumentTestdata' til å opprette journalpost når den ikke finnes for avvikstypen:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test endre fagområde på mottaksregistrert journalpost",
        "dokumentType": "I",
        "fagomrade": "BID",
        "journalstatus":"M"
        }
        """

  Scenario: Skal finne avviket ENDRE_FAGOMRADE på mottaksregistrert journalpost
    Når jeg skal finne avvik med path '/journal/journalpostId/avvik'
    Så skal listen med avvikstyper inneholde 'ENDRE_FAGOMRADE'

  Scenario: Registrere avviket og sjekke endringen av journalpost
    Gitt enhet for behandling av avvik på mottaksregistrert journalpost er '4806'
    Når jeg registrerer avviket med url '/journal/journalpostId/avvik':
        """
        {
          "avvikType":"ENDRE_FAGOMRADE",
          "detaljer": {
            "fagomrade":"FAR"
          }
        }
        """
    Så skal http status være '200'
    Og når jeg jeg henter journalpost etter avviksbehandling med url '/journal/journalpostId'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'fagomrade' = 'FAR'
