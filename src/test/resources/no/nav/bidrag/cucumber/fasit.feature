# language: no
@bidrag-cucumber
Egenskap: bidrag-cucumber: fasit-info
  En deployet applikasjon har informasjon angitt i fasit.adeo.no

  Scenario: Et REST-API skal være operativt når alias for API er oppgitt
    Gitt resttjeneste 'bidragDokument'
    Og jeg bruker miljø: "q0"
    Når det gjøres et kall til 'actuator/health'
    Så skal responsen inneholde json med property 'status' og verdi 'up'

  Scenario: Et REST-API skal være operativt når alias for API er oppgitt
