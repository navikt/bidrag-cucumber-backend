package no.nav.bidrag.cucumber

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Så
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus

class RestTjenesteEgenskap() {
    companion object {
        lateinit var restTjeneste: RestTjeneste
    }

    @Gitt("resttjeneste {string}")
    fun `gitt resttjenste`(alias: String) {
        restTjeneste = RestTjeneste(alias)
    }

    @Så("skal http status være {string}")
    fun `skal http status vaere`(httpStatus: String) {
        val status = HttpStatus.valueOf(httpStatus.toInt())

        assertThat(restTjeneste.httpStatus).`as`("HttpStatus for " + restTjeneste.endpointUrl).isEqualTo(status)
    }

    fun get(endpointUrl: String) {
        restTjeneste.exchangeGet(endpointUrl)
    }

    fun response() = restTjeneste.response
}
