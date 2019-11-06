# language: no
@bidrag-organisasjon
Egenskap: bidrag-organisasjon

  Scenario: Sjekk at health endpoint er operativt
    Gitt resttjenesten 'bidragOrganisasjon' for sjekk av helsedata
    Når jeg kaller helsetjenesten
    Så skal http status for helsesjekken være '200'
    Og header 'content-type' skal være 'application/json'
    Og helseresponsen skal inneholde 'status' = 'UP'

#  Scenario: Sjekk at gyldig saksbehandler-id returnerer OK (200) respons
#    Gitt resttjenesten bidragOrganisasjon
#    Når jeg henter informasjon for ldap ident 'H153959'
#    Så statuskoden skal være '200'
#
#  Scenario: Sjekk at hent av enheter for saksbehandler-id returnerer OK (200) respons
#    Gitt resttjenesten bidragOrganisasjon
#    Når jeg henter enheter for saksbehandler med ident 'Z992022'
#    Så statuskoden skal være '200'
#
#  Scenario: Sjekk at hent av enheter for arbeidsfordeling returnerer OK (200) respons
#    Gitt resttjenesten bidragOrganisasjon
#    Når jeg henter enheter for arbeidsfordeling med diskresjonskode 'SPSF' og geografisk tilknytning '0301'
#    Så statuskoden skal være '200'