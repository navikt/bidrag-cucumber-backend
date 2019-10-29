# language: no
@bidrag-dokument
Egenskap: bidrag-dokument: applikasjon
  Applikasjonen bidrag-dokument er klar for bruk

  Scenario: skal kunne hente informasjon om applikasjonens status
    Når det gjøres et kall til '/actuator/health'
    Så skal responsen inneholde json med property 'status' og verdi 'UP'

  Scenario: skal kunne bruke en operasjon med sikkerhet satt opp
    Når det gjøres et kall til '/sakjournal/0000003?fagomrade=BID'
    Så skal http status ikke være '401' eller '403'
