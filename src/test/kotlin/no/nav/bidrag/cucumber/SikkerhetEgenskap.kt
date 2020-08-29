package no.nav.bidrag.cucumber

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Så
import org.assertj.core.api.Assertions.assertThat

class SikkerhetEgenskap {

    companion object {
        var namespace: String = "na"
    }

    @Gitt("jeg bruker namespace: {string}")
    fun `jeg bruker`(namespace: String) {
        SikkerhetEgenskap.namespace = namespace
    }

    @Så("kan vi hente id token")
    fun `sa kan vi hente id token`() {
        assertThat(Sikkerhet().fetchOnlineIdToken(namespace)).isNotEmpty()
    }
}
