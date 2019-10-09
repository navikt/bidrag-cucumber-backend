# bidrag-cucumber-backend
Integrasjonstester for backend mikrotjenester i bidrag

Kotlin gjør det enkelt å skape lett leselig tester som bruker `Gherkin`-filer (*.feature) med norsk tekst som ligger i `src/test/resources/<pakkenavn>`

`Gherkin`-filene er den funksjonelle beskrivelsen av en automatisert test som bruker BDD (behaviour driven development) for å beskrive de egenskapene som skal støttes
Eks: på en `gherkin` fil på norsk 

```
01: # language: no
02: Egenskap: oppslagstjeneste
03:   <detaljert beskrivelse av egenskapen>
04: 
05:   Scenario: fant ikke person
06:    Gitt liste med "ansatte"
07:    Når man forsøker å finne "Ola"
08:    Så skal svaret være "Ola" er ikke ansatt her
09:
10:  Scenario: fant person
11:    Gitt liste med "ansatte"
12:    Når man forsøker å finne "Per"
15:    Så skal svaret være "Per" er i "kantina"
```

Kort forklart:
- linje 1: språk
- linje 2: egenskapen som testes (feature)
- linje 3: tekstlig beskrivelse av egenskapen
- linje 5: et scenario som denne egenskapen skal støtte
- linje 6: "Gitt" en ressurs
- linje 7: "Når" man utfører noe
- linje 8: "Så" forventer man et resultat

Gitt - Når - Så, er blant kodeordene som `gherkin` reagerer på.
Nøkkelord til å bruk i `gherkin`-filer er `Egenskap`, `Bakgrunn`, `Scenario`, `Eksepmel`, `Abstrakt scenario`, `Gitt`, `Når`, `Og`, `Men`, `Så`, `Eksempler`

Cucumber støtter flere språk og for mer detaljert oversikt over funksjonaliteten som `gherkin` gir, se detaljert beskrivelse på nett: 
<https://cucumber.io/docs/gherkin/reference/>

### Kjøring

Alle kotlin-cucumber-tester kjører på en jvm bruker JUnit som plattform. Derfor kan testene bli utført i hvilken som helt editor som støtter JUnit.

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

Man kan også gjøre både testing og rapportgenerering i et steg med `mvn package`
