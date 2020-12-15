package no.nav.bidrag.cucumber.beregn

import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.FellesEgenskaper
import org.assertj.core.api.Assertions.assertThat

class BeregnEgenskaper {

    @Når("jeg bruker endpoint {string} med json fra fil")
    fun `nar jeg bruker endpoint med json fra fil`(endpoint: String) {
        val json = todoLesFraFil()
        FellesEgenskaper.restTjeneste.exchangePost(endpoint, json)
    }

    @Og("jeg skal få et beregningsresultat")
    fun `jeg skal fa et beregningsresultat`() {
        assertThat(FellesEgenskaper.restTjeneste.hentResponse())
            .containsSequence("resultatKode")
    }

    private fun todoLesFraFil(): String {
        return "todo"
    }
}