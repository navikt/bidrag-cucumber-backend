package no.nav.bidrag.cucumber

import io.cucumber.java.After
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Så
import org.assertj.core.api.Assertions.assertThat

class SikkerhetEgenskap {

    @Gitt("jeg bruker miljø: {string}")
    fun `jeg bruker`(miljo: String) {
        Environment.use(miljo)
    }

    @Så("kan vi hente id token")
    fun `sa kan vi hente id token`() {
        assertThat(Sikkerhet().fetchOnlineIdToken()).isNotEmpty()
    }

    @After
    fun `bruk dynamisk miljo`() {
        if (!Environment.offline) {
            Environment.use(System.getProperty("ENVIRONMENT"))
        }
    }
}
