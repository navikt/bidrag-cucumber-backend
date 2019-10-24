# language: no
@bidrag-dokument
Egenskap: bidrag-dokument: helseinformasjon/sikkerhet
  En deploy av bidrag-dokument er klar for bruk

  Scenario: skal kunne hente informasjon om applikasjonens status
    Gitt resttjenesten bidragDokument
    Når det gjøres et kall til '/actuator/health'
    Så skal responsen inneholde json med property 'status' og verdi 'UP'

  Scenario: skal kunne kalle en get med sikkerhet satt opp
    Gitt resttjenesten bidragDokument
    Når det gjøres et kall til '/sakjournal/0000003?fagomrade=BID'
    Så skal http status ikke være '401' eller '403'
