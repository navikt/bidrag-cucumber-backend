package no.nav.bidrag.cucumber

import io.cucumber.java.no.Gitt

class RestTjenesteEgenskap() {
    companion object {
        lateinit var restTjeneste: RestTjeneste
    }

    @Gitt("resttjeneste {string}")
    fun `gitt resttjenste`(alias: String) {
        restTjeneste = RestTjeneste(alias)
    }

    fun get(endpointUrl: String) {
        restTjeneste.exchangeGet(endpointUrl)
    }

    fun response() = restTjeneste.response
}
