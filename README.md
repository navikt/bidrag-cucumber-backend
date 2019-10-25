# bidrag-cucumber-backend
Integrasjonstester for backend mikrotjenester i bidrag

Kotlin gjør det enkelt å skape lett leselig tester som bruker `Gherkin`-filer (*.feature) med norsk tekst som ligger i `src/test/resources/<pakkenavn>`

BDD (Behaviour driven development) beskrives i `Gherkin`-filene som kjører automatiserte tester på bakgrunnen av funksjonaliteten som skal støttes.
Eks: på en `gherkin` fil på norsk 

```
01: # language: no
02  @oppslagstjenesten
03: Egenskap: oppslagstjeneste
04:   <detaljert beskrivelse av egenskapen>
05: 
06:   Scenario: fant ikke person
07:    Gitt liste med "ansatte"
09:    Når man forsøker å finne "Ola"
09:    Så skal svaret være "Ola" er ikke ansatt her
10:
11:  Scenario: fant person
12:    Gitt liste med "ansatte"
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

### Kjøring

Alle kotlin-cucumber-tester kjører på en jvm og bruker JUnit som plattform. Derfor kan testene bli utført i hvilken som helt editor som støtter JUnit,
samt utføring fra bygg-verktøy som maven.

Testene kan også kjøres fra kommandolinja med maven - `mvn test`

Når `mvn test` kjøres blir alle `gherkin`-filene (*.feature) brukt til å kjøre tester. Det finnes også "tags" som brukes foran egenskaper og scenario.
Det er lagt opp til å "tagge" ei `gherkin`-fil med applikasjonsnavn slik at man kan angi å kjøre tester for en applikasjon alene.
Det er ikke noen begrensninger på hvor mange "tagger" en `Egenskap` eller `Feature` har, eller hvor mange filer som har den samme "taggen".

Kjøring av "taggede" tester:

```
mvn test -Dcucumber.options='--tags "@bidrag-cucumber"'
``` 

For en mer detaljert oversikt over cucumber og api'ene som støttes: <https://cucumber.io/docs/cucumber/api/>  

Det er lagt opp til at testing kan gjøres med valgt applikasjon angitt. Følgende maven kommando blir da utført:

``` 
mvn test -Dcucumber.options='--tags "@bidrag-cucumber or @<valgt-applikasjon>"' 
``` 

Etter at testing er gjennomført så kan man lage en rapport som blir tilgjengelig i `target/generated-report/index.html`. Dette gjøres av en maven-plugin:

```
mvn cluecumber-report:reporting
```

Man kan også gjøre både testing og rapportgenerering i et steg med `mvn install`
