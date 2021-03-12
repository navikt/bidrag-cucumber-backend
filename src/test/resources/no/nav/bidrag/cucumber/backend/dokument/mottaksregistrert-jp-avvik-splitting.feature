# language: no
@bidrag-dokument
@mottaksregistrert
Egenskap: Avvikshendelse BESTILL_SPLITTING på journalposter som er mottaksregistrert (/journal/*/avvik REST API)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt resttjenesten 'bidragDokument' for å registrere avvik på mottaksredigert journalpost, avvikstype 'BESTILL_SPLITTING'
    Og resttjenesten 'bidrag-testdata' til å opprette journalpost når den ikke finnes for avvikstypen:
        """
        {
        "avsenderNavn" : "Cucumber Test",
        "batchNavn"    : "En batch",
        "beskrivelse"  : "Test bestill splitting på mottaksregistrert journalpost",
        "dokumentType" : "I",
        "filnavn"      : "svada.pdf",
        "journalstatus": "M",
        "skannetDato"  : "2019-01-01"
        }
        """

  Scenario: Skal finne avviket BESTILL_SPLITTING på mottaksregistrert journalpost
    Når jeg skal finne avvik med path '/journal/journalpostId/avvik'
    Så skal listen med avvikstyper inneholde 'BESTILL_SPLITTING'

  @ignored
  Scenario: Registrere avviket som fører til at journalposten som nå er slettet
    Gitt enhet for behandling av avvik på mottaksregistrert journalpost er '4806'
    Når jeg registrerer avviket med url '/journal/journalpostId/avvik':
        """
        {
        "avvikType":"BESTILL_SPLITTING",
        "beskrivelse":"etter avsnitt 2"
        }
        """
    Så skal http status være '200'
    Og når jeg jeg henter journalpost etter avviksbehandling med url '/journal/journalpostId'
    Så skal http status være '200'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'AS'
