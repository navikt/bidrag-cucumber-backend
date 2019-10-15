package no.nav.bidrag.cucumber

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Så

class EnvironmentEgenskap {

    @Gitt("jeg bruker miljø: {string}")
    fun `jeg bruker`(miljo: String) {
        Environment.use(miljo)
    }

    @Når("man sjekker at token er gyldig")
    fun `man sjekker at token er gyldig`() {
//        assertThat(Environment().fetchToken()).isOk() todo: implementer og returner et web token og sjekk at det er gyldig
    }

    @Når("man sjekker at bruker-token er gyldig")
    fun `man sjekker at bruker-token er gyldig`() {
//        assertThat(Environment().fetchUsrerToken()).isOk() todo: implementer og returner et bruker-token(?) og sjekk at det er gyldig
    }

    @Så("skal token ha følgende properties:")
    fun `skal token_ha folgende properties`(properties: Map<String, String>) {
        // todo: implementer
    }

}
