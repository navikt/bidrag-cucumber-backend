package no.nav.bidrag.cucumber

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.sikkerhet.IssoTokenManager
import no.nav.bidrag.cucumber.sikkerhet.Sikkerhet
import org.assertj.core.api.Assertions.assertThat

class SikkerhetEgenskap {

    companion object {
        var namespace: String = "na"
    }

    @Gitt("jeg bruker namespace: {string}")
    fun `jeg bruker`(namespace: String) {
        SikkerhetEgenskap.namespace = namespace
    }

    @Så("kan vi hente isso token")
    fun `sa kan vi hente isso token`() {
        assertThat(IssoTokenManager.fetchOnlineIdToken(namespace)).isNotEmpty
    }
}
