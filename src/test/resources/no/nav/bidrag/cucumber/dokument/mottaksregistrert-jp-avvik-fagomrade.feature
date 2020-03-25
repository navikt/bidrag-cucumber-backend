# language: no
@bidrag-dokument
@mottaksregistrert
Egenskap: avvik på journalposter som er mottaksregistrert i bidrag-dokument (/journal/*/avvik REST API)

  Bakgrunn: Gitt resttjeneste, avviksdata og testdata
    Gitt resttjenesten 'bidragDokument'
    Og resttjenesten 'bidragDokumdentTestdata' til å opprette journalpost når den ikke finnes for avvik 'ENDRE_FAGOMRADE':
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test endre fagområde på mottaksregistrert journalpost",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "***REMOVED***",
        "journaldato": "2019-01-01",
        "journalforendeEnhet": "1289",
        "journalfortAv": "Behandler, Zakarias",
        "mottattDato": "2019-01-01",
        "skannetDato": "2019-01-01",
        "saksnummer": "0000003"
        }
        """

