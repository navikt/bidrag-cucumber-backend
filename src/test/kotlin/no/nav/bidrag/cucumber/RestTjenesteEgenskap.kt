package no.nav.bidrag.cucumber

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Så
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.collections.HashMap

class RestTjenesteEgenskap() {
    companion object Manage {
        private lateinit var alias: String
        private val restTjenester = HashMap<String, RestTjeneste>()

        private fun hentHttpStatus() = restTjenester.get(alias)?.httpStatus
        private fun hentEndpointUrl() = restTjenester.get(alias)?.endpointUrl
    }

    @Gitt("resttjeneste {string}")
    fun `gitt resttjenste`(alias: String) {
        if (!restTjenester.containsKey(alias)) {
            restTjenester.put(alias, RestTjeneste(alias))
        }

        Manage.alias = alias
    }

    @Så("skal http status ikke være {string} eller {string}")
    fun `skal http status ikke vaere`(enHttpStatus: String, enAnnenHttpStatus: String) {
        val httpStatus = HttpStatus.valueOf(enHttpStatus.toInt())
        val annenHttpStatus = HttpStatus.valueOf(enAnnenHttpStatus.toInt())

        assertThat(hentHttpStatus()).`as`("HttpStatus for " + hentEndpointUrl()).isNotIn(EnumSet.of(httpStatus, annenHttpStatus))
    }

    @Så("skal http status være {string}")
    fun `skal http status vaere`(httpStatus: String) {
        val status = HttpStatus.valueOf(httpStatus.toInt())

        assertThat(restTjeneste.httpStatus).isEqualTo(status)
    }

    fun get(endpointUrl: String) {
        if (restTjenester.containsKey(alias)) {
            restTjenester.get(alias)!!.exchangeGet(endpointUrl)
        } else {
            throw IllegalStateException("Resttjeneste for $alias er ikke satt")
        }
    }

    fun response() = restTjenester.get(alias)?.response
}
