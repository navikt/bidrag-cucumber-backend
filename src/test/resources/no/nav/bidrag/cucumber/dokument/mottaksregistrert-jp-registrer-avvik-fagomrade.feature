# language: no
@bidrag-dokument
@mottaksregistrert
Egenskap: registrere avviket ENDRE_FAGOMRADE på journalposter som er mottaksregistrert i bidrag-dokument (/journal/*/avvik REST API)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt resttjenesten 'bidragDokument' for å registrere avvik på mottaksredigert journalpost, avvikstype 'ENDRE_FAGOMRADE'
    Og resttjenesten 'bidragDokumentTestdata' til å opprette journalpost når den ikke finnes for avvikstypen:
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

  Scenario: Registrere avviket og sjekke endringen av journalpost
    Gitt enhet for behandling av avvik på mottaksregistrert journalpost er '4806'
    Når jeg registrerer avviket med url '/journal/journalpostId/avvik':
        """
        {
        "avvikType":"ENDRE_FAGOMRADE",
        "enhetsnummer":"4806",
        "beskrivelse":"FAR"
        }
        """
    Så skal http status være '200'
    Og når jeg jeg henter journalpost etter avviksbehandling med url '/journal/journalpostId'
    Så skal responsen inneholde 'fagomrade' = 'FAR'
