# language: no
@bidrag-dokument
Egenskap: bidrag-dokument: fasit-info
  En deployet applikasjon har informasjon angitt i fasit.adeo.no

  Scenario: Et REST-API skal være operativt når alias for API er oppgitt
    Gitt resttjeneste 'bidragDokument'
    Og jeg bruker miljø: "q0"
    Når det gjøres et kall til '/actuator/health'
    Så skal responsen inneholde json med property 'status' og verdi 'UP'

  Scenario: skal kunne kalle en http:get på en rest tjeneste med sikkerhet satt opp
    Gitt resttjeneste 'bidragDokument'
    Og jeg bruker miljø: "q0"
    Når det gjøres et kall til '/sakjournal'
    Så skal html responsen være '200'
