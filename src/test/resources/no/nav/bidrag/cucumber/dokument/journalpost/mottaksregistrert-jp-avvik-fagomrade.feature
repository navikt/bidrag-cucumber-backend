# language: no
@bidrag-dokument-journalpost
@mottaksregistrert
Egenskap: avviket ENDRE_FAGOMRADE på journalposter som er mottaksregistrert i bidrag-dokument (/journal/*/avvik REST API)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt resttjenesten 'bidragDokumentJournalpost' for avviksbehandling av mottaksredigert journalpost for avvikstype 'ENDRE_FAGOMRADE'
    Og resttjenesten 'bidragDokumentTestdata' til å opprette journalpost når den ikke finnes for avvikstype
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test endre fagområde på mottaksregistrert journalpost",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "29118044353",
        "journaldato": "2019-01-01",
        "journalstatus":"M",
        "journalforendeEnhet": "1289",
        "journalfortAv": "Behandler, Zakarias",
        "mottattDato": "2019-01-01",
        "skannetDato": "2019-01-01",
        "saksnummer": "0000003"
        }
        """

  Scenario: Skal finne finne avvikstype på mottaksregistrert journalpost
    Når jeg skal finne avvik med path '/journal/journalpostId/avvik'
    Så skal listen med avvikstyper inneholde 'ENDRE_FAGOMRADE'
