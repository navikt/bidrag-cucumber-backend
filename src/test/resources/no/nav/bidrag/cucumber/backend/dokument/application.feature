# language: no
@bidrag-dokument
@sakjournal
Egenskap: bidrag-dokument: applikasjon
  Applikasjonen bidrag-dokument er klar for bruk

  Bakgrunn: Felles for scenarioer
    Gitt resttjenesten 'bidragDokument'

  Scenario: skal kunne hente informasjon om applikasjonens status
    Når jeg kaller helsetjenesten
    Så skal http status være '200'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario: skal kunne bruke en operasjon med sikkerhet satt opp
    Når det gjøres et kall til '/sak/0000003/journal?fagomrade=BID'
    Så skal http status ikke være '401' eller '403'
