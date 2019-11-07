package no.nav.bidrag.cucumber

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.springframework.http.HttpStatus

class FellesEgenskaper {
    companion object {
        lateinit var restTjeneste: RestTjeneste
    }

    @Gitt("resttjenesten {string}")
    fun resttjenesten(alias: String) {
        restTjeneste = RestTjeneste(alias)
    }

    @Så("skal http status være {string}")
    fun `skal http status vaere`(enHttpStatus: String) {
        val httpStatus = HttpStatus.valueOf(enHttpStatus.toInt())

        assertThat(restTjeneste.hentHttpStatus()).isEqualTo(httpStatus)
    }

    @Og("hvert element i listen skal ha følgende properties satt:")
    fun `hvert element i listen skal ha folgende properties satt`(properties: List<String>) {
        val verifyer = SoftAssertions()
        val responseObject = restTjeneste.hentResponseSomListe()

        responseObject.forEach { element ->
            properties.forEach { verifyer.assertThat(element).`as`("missing $it in jp: ${element["journalpostId"]})").containsKey(it) }
        }

        verifyer.assertAll()
    }
}