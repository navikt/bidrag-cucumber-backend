# language: no
@bidrag-dokument-journalpost
@mottaksregistrert
Egenskap: avvik bidrag-dokument-journalpost: endre inn til utgående på mottaksregistrert journalpost

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt resttjenesten 'bidragDokumentJournalpost' for å registrere avvik på mottaksredigert journalpost, avvikstype 'INNG_TIL_UTG_DOKUMENT'
    Og resttjenesten 'bidrag-testdata' til å opprette journalpost når den ikke finnes for avvikstypen:
        """
        {
        "avsenderNavn"   : "Cucumber Test",
        "beskrivelse"    : "Test bestill inn til utgående på mottaksregistrert journalpost",
        "dokumentType"   : "I",
        "journalstatus"  : "M"
        }
        """


  Scenario: Skal finne avviket INNG_TIL_UTG_DOKUMENT på mottaksregistrert journalpost
    Når jeg skal finne avvik med path '/journal/journalpostId/avvik?journalstatus=M'
    Så skal listen med avvikstyper inneholde 'INNG_TIL_UTG_DOKUMENT'

  Scenario: Registrere avviket på journalposten, og sjekk at dokumenttypen er endret til utgående.
    Gitt enhet for behandling av avvik på mottaksregistrert journalpost er '4806'
    Når jeg registrerer avviket med url '/journal/journalpostId/avvik':
        """
        {
        "avvikType":"INNG_TIL_UTG_DOKUMENT"
        }
        """
    Så skal http status være '200'
    Og når jeg jeg henter journalpost etter avviksbehandling med url '/journal/journalpostId'
    Så skal http status være '200'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'dokumentType' = 'U'
