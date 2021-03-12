# language: no
@bidrag-dokument-journalpost
@mottaksregistrert
Egenskap: Avvikshendelse BESTILL_RESKANNING på journalposter som er mottaksregistrert (/journal/*/avvik REST API)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt resttjenesten 'bidragDokumentJournalpost' for å registrere avvik på mottaksredigert journalpost, avvikstype 'BESTILL_RESKANNING'
    Og resttjenesten 'bidrag-testdata' til å opprette journalpost når den ikke finnes for avvikstypen:
        """
        {
        "avsenderNavn" : "Cucumber Test",
        "batchNavn"    : "En batch",
        "beskrivelse"  : "Test bestill reskanning på mottaksregistrert journalpost",
        "dokumentType" : "I",
        "journalstatus": "M",
        "skannetDato"  : "2019-01-01"
        }
        """

  Scenario: Skal finne avviket BESTILL_RESKANNING på mottaksregistrert journalpost
    Når jeg skal finne avvik med path '/journal/journalpostId/avvik?journalstatus=M'
    Så skal listen med avvikstyper inneholde 'BESTILL_RESKANNING'

  @ignored
  Scenario: Registrere avviket som fører til at journalposten som nå er slettet
    Gitt enhet for behandling av avvik på mottaksregistrert journalpost er '4806'
    Når jeg registrerer avviket med url '/journal/journalpostId/avvik':
        """
        {
        "avvikType":"BESTILL_RESKANNING"
        }
        """
    Så skal http status være '200'
    Og når jeg jeg henter journalpost etter avviksbehandling med url '/journal/journalpostId'
    Så skal http status være '200'
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'AR'
