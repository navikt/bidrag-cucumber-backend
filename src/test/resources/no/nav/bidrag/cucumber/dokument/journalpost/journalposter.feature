# language: no
@bidrag-dokument-journalpost
Egenskap: bidrag-dokument-journalpost

    Bakgrunn: Spesifiser base-url til tjenesten her så vi slipper å gjenta for hvert scenario.
        Gitt resttjenesten 'bidragDokumentJournalpost'

    Scenario: Sjekk at vi får korrekt basisinnhold journalpost for en gitt journalpostId
        Gitt jeg henter journalpost for sak "0000003" som har id "BID-19650256"
        Så skal http status være '200'
        Og responsen skal inneholde et objekt med navn 'journalpost' som har feltene:
            | avsenderNavn  |
            | dokumentDato  |
            | dokumentType  |
            | journalstatus |
            | journalfortAv |
            | innhold       |

    Scenario: Sjekk at vi får korrekt data i 'dokumenter' for en gitt journalpostId
        Gitt jeg henter journalpost for sak "0000003" som har id "BID-19650256"
        Så skal http status være '200'
        Og responsen skal inneholde et objekt med navn 'journalpost' som har et felt 'dokumenter' med feltene:
            | dokumentreferanse |
            | dokumentType      |
            | tittel            |

    Scenario: Sjekk at vi får korrekt gjelderAktor for en gitt journalpostId
        Gitt jeg henter journalpost for sak "0000003" som har id "BID-32352090"
        Så skal http status være '200'
        Og responsen skal inneholde et objekt med navn 'journalpost' som har et felt 'gjelderAktor' med feltet 'ident'

    Scenario: Sjekk at ukjent id gir 404
        Gitt jeg henter journalpost for sak "0000003" som har id "BID-12345"
        Så skal http status være '404'

    Scenario: Sjekk at id uten prefix gir 400
        Gitt jeg henter journalpost for sak "0000003" som har id "32352090"
        Så skal http status være '400'

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
        Så skal http status være '202'
        Og jeg henter journalpost for sak "0000004" som har id "BID-30040789"
        Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'avsenderNavn' = 'Strutle, Sylfest'

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
        Så skal http status være '202'
        Og jeg henter journalpost for sak "0000004" som har id "BID-30040789"
        Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'avsenderNavn' = 'Bær, Bjarne'

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
        Så skal http status være '202'
        Og jeg henter journalpost for sak "0000004" som har id "BID-30040789"
        Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'dokumentDato' = '2001-01-01'

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
        Så skal http status være '202'
        Og jeg henter journalpost for sak "0000004" som har id "BID-30040789"
        Og responsen skal inneholde et objekt med navn 'journalpost' som har feltet 'dokumentDato' = '2001-02-01'

    Scenario: Sjekk at sak som ikke finnes git HttpStatus 404: Not Found
        Gitt jeg henter journalpost for sak "XYZ" som har id "BID-1234"
        Så skal http status være '404'
