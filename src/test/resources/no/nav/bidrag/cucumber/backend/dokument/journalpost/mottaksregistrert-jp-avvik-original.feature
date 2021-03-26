# language: no
@bidrag-dokument-journalpost
@mottaksregistrert
Egenskap: Avvikshendelse BESTILL_ORIGINAL på journalposter som er mottaksregistrert (/journal/*/avvik REST API)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt resttjenesten 'bidragDokumentJournalpost' for å registrere avvik på mottaksredigert journalpost, avvikstype 'BESTILL_ORIGINAL'
    Og resttjenesten 'bidrag-testdata' til å opprette journalpost når den ikke finnes for avvikstypen:
      """
        {
          "avsenderNavn"       : "Cucumber Test",
          "batchNavn"          : "En batch",
          "beskrivelse"        : "Test bestill reskanning på mottaksregistrert journalpost",
          "dokumentType"       : "I",
          "journalforendeEnhet": "4806",
          "journalstatus"      : "M",
          "originalBestilt"    : "false",
          "skannetDato"        : "2019-01-01"
        }
      """

  Scenario: Skal finne avviket BESTILL_ORIGINAL på mottaksregistrert journalpost
    Når jeg skal finne avvik med path '/journal/journalpostId/avvik?journalstatus=M'
    Så skal listen med avvikstyper inneholde 'BESTILL_ORIGINAL'

  Scenario: Registrere avviket på journalposten og sjekk at vi kan hente journalposten.
    Gitt enhet for behandling av avvik på mottaksregistrert journalpost er '4806'
    Når jeg registrerer avviket med url '/journal/journalpostId/avvik':
      """
        {
          "avvikType":"BESTILL_ORIGINAL"
        }
      """
    Så skal http status være '200'
    Og når jeg jeg henter journalpost etter avviksbehandling med url '/journal/journalpostId'
    Så skal http status være '200'

  Scenario: Sjekk at avviksvalg for gitt journalpost ikke inneholder BESTILL_ORIGINAL
    Når jeg skal finne avvik med path '/journal/journalpostId/avvik?journalstatus=M'
    Så skal http status være '200'
    Og så skal listen med avvikstyper ikke inneholde 'BESTILL_ORIGINAL'

  Scenario: Sjekk at oppgave blir laget for bestill original
    Gitt jeg søker etter oppgaver for mottaksregistrert journalpost
    Så skal http status for oppgavesøket være '200'
    Og søkeresultatet skal inneholde en oppgave
