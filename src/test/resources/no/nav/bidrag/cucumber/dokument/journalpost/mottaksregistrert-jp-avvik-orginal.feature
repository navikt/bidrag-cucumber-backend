# language: no
@bidrag-dokument-journalpost
@mottaksregistrert
Egenskap: Avvikshendelse BESTILL_ORGINAL på journalposter som er mottaksregistrert (/journal/*/avvik REST API)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt resttjenesten 'bidragDokumentJournalpost' for å registrere avvik på mottaksredigert journalpost, avvikstype 'BESTILL_ORGINAL'
    Og resttjenesten 'bidragDokumentTestdata' til å opprette journalpost når den ikke finnes for avvikstypen:
        """
        {
        "avsenderNavn"   : "Cucumber Test",
        "batchNavn"      : "En batch",
        "beskrivelse"    : "Test bestill reskanning på mottaksregistrert journalpost",
        "dokumentType"   : "I",
        "journalstatus"  : "M",
        "originalBestilt": "false",
        "skannetDato"    : "2019-01-01"
        }
        """

  Scenario: Skal finne finne avvikstype på mottaksregistrert journalpost
    Når jeg skal finne avvik med path '/journal/journalpostId/avvik'
    Så skal listen med avvikstyper inneholde 'BESTILL_ORGINAL'

  Scenario: Registrere avviket som fører til at journalposten som nå er slettet (og derfor ikke kan hentes som mottaksregistrert journalpost)
    Gitt enhet for behandling av avvik på mottaksregistrert journalpost er '4806'
    Når jeg registrerer avviket med url '/journal/journalpostId/avvik':
        """
        {
        "avvikType":"BESTILL_ORGINAL
        }
        """
    Så skal http status være '201'
    Og når jeg jeg henter journalpost etter avviksbehandling med url '/journal/journalpostId'
    Så skal http status være '200'
    Og responsen skal inneholde 'orginalBestilt' = 'true'

  Scenario: Sjekk at oppgave blir laget for bestill original
    Gitt jeg søker etter oppgaver for mottaksregistrert journalpost
    Så skal http status for oppgavesøket være '200'
    Og søkeresultatet skal inneholde en oppgave
