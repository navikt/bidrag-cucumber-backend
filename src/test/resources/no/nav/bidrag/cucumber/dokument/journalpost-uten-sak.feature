# language: no
@bidrag-dokument
Egenskap: journalposter uten sak for bidrag-dokument (/journal/* REST API)

  Tester REST API for journalposter uten sak i bidrag-dokument.

  Bakgrunn: Tester hent journalpost for en gitt id: BID-37616688
    Gitt resttjenesten 'bidragDokument'

  Scenario: Hent en journalpost uten sakstilknytning med ugyldig prefix i journalpost id
    Gitt at jeg henter journalpost med path '/journal/XXX-123'
    Så skal http status være '400'

  Scenario: Hent en journalpost uten sakstilknytning med ukjent journalpost id
    Gitt at jeg henter journalpost med path '/journal/BID-123'
    Så skal http status være '404'

  Scenario: Hent en journalpost uten sakstilknytning
    Gitt at jeg henter journalpost med path '/journal/BID-37616688'
    Så skal http status være '200'
