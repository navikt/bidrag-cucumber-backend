# language: no
@bidrag-dokument-journalpost
@mottaksregistrert
Egenskap: Avvikshendelse ENDRE_FAGOMRADE på journalposter som er mottaksregistrert (/journal/*/avvik REST API)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt resttjenesten 'bidragDokumentJournalpost' for å registrere avvik på mottaksredigert journalpost, avvikstype 'ENDRE_FAGOMRADE'
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

  Scenario: Skal finne finne avvikstype på mottaksregistrert journalpost
    Når jeg skal finne avvik med path '/journal/journalpostId/avvik'
    Så skal listen med avvikstyper inneholde 'ENDRE_FAGOMRADE'

  Scenario: Registrere avviket og sjekke endringen av journalpost
    Gitt enhet for behandling av avvik på mottaksregistrert journalpost er '4806'
    Når jeg registrerer avviket med url '/journal/journalpostId/avvik':
        """
        {
        "avvikType":"ENDRE_FAGOMRADE",
        "beskrivelse":"FAR"
        }
        """
    Så skal http status være '200'
    Og når jeg jeg henter journalpost etter avviksbehandling med url '/journal/journalpostId'
    Så skal responsen inneholde 'fagomrade' = 'FAR'