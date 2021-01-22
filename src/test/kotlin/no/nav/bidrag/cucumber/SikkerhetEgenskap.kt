package no.nav.bidrag.cucumber

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.sikkerhet.AzureTokenManager
import no.nav.bidrag.cucumber.sikkerhet.IssoTokenManager
import org.assertj.core.api.Assertions.assertThat

class SikkerhetEgenskap {

    companion object {
        private var namespace: String = "na"
        private lateinit var applicationName: String
    }

    @Gitt("jeg bruker namespace: {string}")
    fun `jeg bruker`(namespace: String) {
        SikkerhetEgenskap.namespace = namespace
    }

    @Så("kan vi hente isso token")
    fun `sa kan vi hente isso token`() {
        assertThat(IssoTokenManager.fetchOnlineIdToken(namespace)).isNotEmpty
    }

    @Gitt("jeg bruker applikasjonen: {string}")
    fun jeg_bruker_applikasjonen(applicationName: String) {
        NaisConfiguration.read(applicationName)
        SikkerhetEgenskap.applicationName = applicationName
    }

    @Så("kan vi hente azure token")
    fun kan_vi_hente_azure_token() {
        assertThat(AzureTokenManager.fetchToken(applicationName)).isNotEmpty
    }
}
