package no.nav.bidrag.cucumber

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Så

class SikkerhetEgenskap {

    @Gitt("jeg bruker miljø: {string}")
    fun `jeg bruker`(miljo: String) {
        Environment.use(miljo)
    }

    @Så("kommer det ikke noen exception ved henting av id token")
    fun `sa kommer det ikke noen exception ved henting av id token`() {
        Sikkerhet().fetchIdToken()
    }
}
