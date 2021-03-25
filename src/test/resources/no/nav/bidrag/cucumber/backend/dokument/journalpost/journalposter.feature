# language: no
@bidrag-dokument-journalpost
Egenskap: bidrag-dokument-journalpost

    Bakgrunn: Spesifiser resttjeneste og testdata grunnlag.
        Gitt resttjenesten 'bidragDokumentJournalpost'
        Og resttjenesten 'bidrag-testdata' for manipulering av testdata
        Og opprett journalpost med nøkkel 'JOURNALPOSTER_BDJ' når den ikke finnes:
            """
            {
              "avsenderNavn": "Cucumber Test",
              "beskrivelse": "journalposter feature",
              "dokumentType": "I",
              "dokumentdato": "2019-01-01",
              "dokumentreferanse": "1234567890",
              "fagomrade": "BID",
              "journalfortAv": "Bond, James",
              "journalstatus": "J",
              "gjelder": "29118044353",
              "innhold": "your mama",
              "journaldato": "2019-01-01",
              "mottattDato": "2019-01-01",
              "skannetDato": "2019-01-01",
              "saksnummer": "0000003",
              "tittel": "nothing else matters..."
            }
            """
        Og lag bidragssak '0000003' når den ikke finnes fra før:
            """
            {
              "saksnummer": "0000003",
              "enhetsnummer": "4802"
            }
            """

    Scenario: Sjekk at vi får korrekt basisinnhold journalpost for en gitt journalpostId
        Og jeg henter journalpost for sak '0000003' som har id for nokkel 'JOURNALPOSTER_BDJ'
        Så skal http status være '200'
        Og responsen skal inneholde et objekt med navn 'journalpost' som har feltene:
            | avsenderNavn  |
            | dokumentDato  |
            | dokumentType  |
            | journalstatus |
            | journalfortAv |
            | innhold       |

    Scenario: Sjekk at vi får korrekt data i 'dokumenter' for en gitt journalpostId
        Gitt jeg henter journalpost for sak '0000003' som har id for nokkel 'JOURNALPOSTER_BDJ'
        Så skal http status være '200'
        Og responsen skal inneholde et objekt med navn 'journalpost' som har et felt 'dokumenter' med feltene:
            | dokumentreferanse |
            | dokumentType      |
            | tittel            |

    @ignored
    Scenario: Sjekk at vi får korrekt gjelderAktor for en gitt journalpostId
        Gitt jeg henter journalpost for sak "0000003" som har id "BID-32352090"
        Så skal http status være '200'
        Og responsen skal inneholde et objekt med navn 'journalpost' som har et felt 'gjelderAktor' med feltet 'ident'

    @ignored
    Scenario: Sjekk at ukjent id gir 404
        Gitt jeg henter journalpost for sak "0000003" som har id "BID-12345"
        Så skal http status være '404'

    Scenario: Sjekk at id uten prefix gir 400
        Gitt jeg henter journalpost for sak "0000003" som har id "32352090"
        Så skal http status være '400'

    @ignored
    Scenario: Sjekk at journalpost kan oppdateres - Sylfest Strutle
        Gitt jeg endrer journalpost med id 'BID-30040789' til:
            """
            {
            "journalpostId": 30040789,
            "saksnummer": {
            "erTilknyttetNySak": false,
            "saksnummer": "0000004",
            "saksnummerSomSkalErstattes":
            "0000004"
            },
            "gjelder": "29118044353",
            "avsenderNavn": "Strutle, Sylfest",
            "beskrivelse": "Søknad, Bidrag",
            "journaldato": "2006-05-09"
            }
            """
        Så skal http status være '200'
        Og jeg henter journalpost for sak "0000004" som har id "BID-30040789"
        Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'avsenderNavn' = 'Strutle, Sylfest'

    @ignored
    Scenario: Sjekk at journalpost kan oppdateres - Bjarne Bær
        Gitt jeg endrer journalpost med id 'BID-30040789' til:
            """
            {
            "journalpostId": 30040789,
            "saksnummer": {
            "erTilknyttetNySak": false,
            "saksnummer": "0000004",
            "saksnummerSomSkalErstattes":
            "0000004"
            },
            "gjelder": "29118044353",
            "avsenderNavn": "Bær, Bjarne",
            "beskrivelse": "Søknad, Bidrag",
            "journaldato": "2006-05-09"
            }
            """
        Så skal http status være '200'
        Og jeg henter journalpost for sak "0000004" som har id "BID-30040789"
        Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'avsenderNavn' = 'Bær, Bjarne'

    @ignored
    Scenario: Sjekk at dokumentDato kan oppdateres til 2001-01-01
        Gitt jeg endrer journalpost med id 'BID-30040789' til:
            """
            {
            "journalpostId": 30040789,
            "saksnummer": {
            "erTilknyttetNySak": false,
            "saksnummer": "0000004",
            "saksnummerSomSkalErstattes":
            "0000004"
            },
            "gjelder": "29118044353",
            "avsenderNavn": "Bær, Bjarne",
            "beskrivelse": "Søknad, Bidrag",
            "journaldato": "2006-05-09",
            "dokumentDato": "2001-01-01"
            }
            """
        Så skal http status være '200'
        Og jeg henter journalpost for sak "0000004" som har id "BID-30040789"
        Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'dokumentDato' = '2001-01-01'

    @ignored
    Scenario: Sjekk at dokumentDato kan oppdateres til 2001-02-01
        Gitt jeg endrer journalpost med id 'BID-30040789' til:
            """
            {
            "journalpostId": 30040789,
            "saksnummer": {
            "erTilknyttetNySak": false,
            "saksnummer": "0000004",
            "saksnummerSomSkalErstattes":
            "0000004"
            },
            "gjelder": "29118044353",
            "avsenderNavn": "Bær, Bjarne",
            "beskrivelse": "Søknad, Bidrag",
            "journaldato": "2006-05-09",
            "dokumentDato": "2001-02-01"
            }
            """
        Så skal http status være '200'
        Og jeg henter journalpost for sak "0000004" som har id "BID-30040789"
        Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'dokumentDato' = '2001-02-01'

    Scenario: Sjekk at sak som ikke finnes git HttpStatus 404: Not Found
        Gitt jeg henter journalpost for sak "XYZ" som har id "BID-1234"
        Så skal http status være '404'
