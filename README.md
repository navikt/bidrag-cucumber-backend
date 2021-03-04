# bidrag-cucumber-backend
Integrasjonstester for backend mikrotjenester i bidrag

## workflow
![](https://github.com/navikt/bidrag-cucumber-backend/workflows/continuous%20integration/badge.svg)

## beskrivelse

Kotlin gjør det enkelt å skape lett leselig tester og dette er satt opp med `Gherkin`-filer (*.feature) som har norsk tekst og ligger i `src/test/resources/<pakkenavn>`

BDD (Behaviour driven development) beskrives i `Gherkin`-filene (`*.featue`) som kjører automatiserte tester på bakgrunnen av funksjonaliteten som skal støttes.
Eks: på en `gherkin` fil på norsk 

```
01: # language: no
02  @oppslagstjenesten
03: Egenskap: oppslagstjeneste
04:   <detaljert beskrivelse av egenskapen>
05: 
06:   Scenario: fant ikke person
07:    Gitt liste over "tidligere ansatte"
09:    Når man forsøker å finne "Ola"
09:    Så skal svaret være "Ola" er ikke ansatt her
10:
11:  Scenario: fant person
12:    Gitt liste over "ansatte"
13:    Når man forsøker å finne "Per"
14:    Så skal svaret være "Per" er i "kantina"
```

Kort forklart:
- linje 1: språk
- linje 2: "tag" av test
- linje 3: egenskapen som testes (feature)
- linje 4: tekstlig beskrivelse av egenskapen
- linje 6: et scenario som denne egenskapen skal støtte
- linje 7: "Gitt" en ressurs
- linje 8: "Når" man utfører noe
- linje 9: "Så" forventer man et resultat
- linje 11: et annet scenario som denne egenskapen skal støtte
- linje 12-14: "Gitt" - "Når" - "Så": forventet oppførsel til dette scenarioet

Norske kodeord for `gherkin`: `Gitt` - `Når` - `Så` er de fremste kodeordene for norsk BDD.
Alle norske nøkkelord som kan brukes i `gherkin`-filer er `Egenskap`, `Bakgrunn`, `Scenario`, `Eksepmel`, `Abstrakt scenario`, `Gitt`, `Når`, `Og`, `Men`, `Så`, `Eksempler`

Cucumber støtter flere språk og for mer detaljert oversikt over funksjonaliteten som `gherkin` gir, se detaljert beskrivelse på nett: 
<https://cucumber.io/docs/gherkin/reference/>

### GitHub Workflow

Cucumber testene brukes i github workflow når en push blir gjort til en branch eller main på en nais applikasjon under bidrag. Hvis en endring av en
nais applikasjon krever endringer av cucumber testene, så kod disse endringene i en feature branch i bidrag-cucumber-backend (med samme navn som som
branchen på applikasjonen som du tester) og denne branchen blir automatisk plukket opp av github workflow når cucumber-testene gjøres på en hvilken
som helst feature-branch tilhørende en nais-applikasjonen.

- action for oppsett av cucumber kjøremiljø: navikt/bidrag-integration/cucumber-clone@v4
- action for kjøring av cucumber           : navikt/bidrag-maven/cucumber-backend@v5
 
### Kjøring

Alle kotlin-cucumber-tester kjører på en jvm og bruker JUnit som plattform. Derfor kan testene bli utført i hvilken som helt editor som støtter JUnit,
samt utføring fra bygg-verktøy som maven.

Testene kan også kjøres fra kommandolinja med maven - `mvn test`

Når `mvn test` kjøres blir alle `gherkin`-filene (*.feature) brukt til å kjøre tester. Det finnes også "tags" som brukes foran egenskaper og scenario.
Det er lagt opp til å "tagge" ei `gherkin`-fil med applikasjonsnavn slik at man kan angi å kjøre tester for en applikasjon alene.
Det er ikke noen begrensninger på hvor mange "tagger" en `Egenskap` eller `Feature` har, eller hvor mange filer som har den samme "taggen".

Kjøring av "taggede" tester:

```
mvn test -Dcucumber.filter.tags="@cucumber"
``` 

For en mer detaljert oversikt over cucumber og api'ene som støttes: <https://cucumber.io/docs/cucumber/api/>  

Det er lagt opp til at testing kan gjøres med valgt applikasjon angitt. Følgende maven kommando blir da utført:

``` 
export CUCUMBER_FILTER_TAGS="@<nais applikasjon> and not @ignored"
mvn test ... 
``` 
Parametre til applikasjonen gies i hovedsak som fil i json. Unntaket er passord (inkl. nav ident)
Eksempel på `integrationInput.json` (applikasjoner som støtter azure må ha client id, client secret og tennant som ligger på kubernetes poddene):
``` 
{
  "azureInputs":[{
    "name": "bidrag-dokument-feature",
    "clientId": "<fra kubernetes pod>",
    "clientSecret": "<fra kubernetes pod>",
    "tenant": "<fra kubernetes pod>"
  },{
    "name": "bidrag-sak-feature",
    "clientId": "<fra kubernetes pod>",
    "clientSecret": "<fra kubernetes pod>",
    "tenant": "<fra kubernetes pod>"
  }],
  "environment":"feature",
  "naisProjectFolder":"apps",
  "userTest":"z992903"
}
``` 
* `environemnt` skal være main eller feature (main-branch eller en feature-branch som testes)
* `naisProjectFolder` skal være relativ sti fra cucumber prosjektet som inneholder nais konfigurasjon til prosjekte(t/ne) som testes
* `userTest` skal være nav test bruker som blir autentisert mot azure (og derfor ikke trenger medfølgende nav-ident)

En fullstendig kjøring av alle prosjekter er derfor:
```
mvn test -e -DUSERNAME=j104364 -DINTEGRATION_INPUT=json/integrationInput.json \
  -DUSER_AUTH=$USER_AUTHENTICATION \
  -DTEST_AUTH=$TEST_USER_AUTHENTICATION \
  -DPIP_AUTH=$PIP_USER_AUTHENTICATION
```
_**PS!**_ *PIP_AUTH* brukes bare i en test tilhørende `bidrag-sak`

_**PPS!**_ *USERNAME* (som er navident) må foreløpig være del av testingen siden ikke alle applikasjoner er skrudd over til azure enda.

#### Kjøring lokalt
`bidrag-cucumber-backend` kan også kjøres lokalt, men bare hvis man har kontakt med applikasjonen via terminal. Dvs. enten fra "Nord-Korea" eller fra
et naisdevice.

### Test rapportering
Etter at testing er gjennomført så kan man lage en rapport som blir tilgjengelig i `target/generated-report/index.html`. Dette gjøres av en
maven-plugin:
```
mvn cluecumber-report:reporting
```

Man kan også gjøre både testing og rapportgenerering i et steg med `mvn install`
