# language: no
@bidrag-dokument
Egenskap: bidrag-dokument: fasit-info
  En deployet applikasjon har informasjon angitt i fasit.adeo.no

  Scenario: Et REST-API skal være operativt når alias for API er oppgitt
    Gitt resttjeneste 'bidragDokument'
    Når det gjøres et kall til '/actuator/health'
    Så skal responsen inneholde json med property 'status' og verdi 'UP'

  Scenario: skal kunne kalle en http:get på en rest tjeneste med sikkerhet satt opp
    Gitt resttjeneste 'bidragDokument'
    Når det gjøres et kall til '/sakjournal/0000003?fagomrade=BID'
    Så skal http status ikke være '401' eller '403'
