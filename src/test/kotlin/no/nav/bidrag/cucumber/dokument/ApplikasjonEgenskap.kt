package no.nav.bidrag.cucumber.dokument

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.RestTjeneste
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus
import java.util.EnumSet

private lateinit var restTjeneste: RestTjeneste

class ApplikasjonEgenskap {

    @Gitt("resttjenesten bidragDokument")
    fun `gitt resttjensten`() {
        restTjeneste = RestTjenesteDokument()
    }

    @Når("det gjøres et kall til {string}")
    fun `det gjores et kall til`(endpointUrl: String) {
        restTjeneste.exchangeGet(endpointUrl)
    }

    @Så("skal responsen inneholde json med property {string} og verdi {string}")
    fun `skal responsen inneholde json med property og verdi`(property: String, verdi: String) {
        assertThat(restTjeneste.hentResponse()).contains(""""$property":"$verdi"""")
    }

    @Så("skal aktuell klasse være {string}")
    fun `skal aktuell klasse vaere`(forventetEgenskap: String) {
        assertThat(this.javaClass.simpleName).isEqualTo(forventetEgenskap)
    }

    @Så("skal http status ikke være {string} eller {string}")
    fun `skal http status ikke vaere`(enHttpStatus: String, enAnnenHttpStatus: String) {
        val httpStatus = HttpStatus.valueOf(enHttpStatus.toInt())
        val annenHttpStatus = HttpStatus.valueOf(enAnnenHttpStatus.toInt())

        assertThat(restTjeneste.hentHttpStatus()).`as`("HttpStatus for " + restTjeneste.hentEndpointUrl())
                .isNotIn(EnumSet.of(httpStatus, annenHttpStatus))
    }
}
