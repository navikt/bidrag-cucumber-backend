package no.nav.bidrag.cucumber

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Så
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json



class FasitEgenskap {

    private lateinit var restTjeneste: RestTjeneste

    @Gitt("resttjeneste {string}")
    fun `gitt resttjenste`(alias: String) {
        restTjeneste = RestTjeneste(alias)
    }

    @Når("det gjøres et kall til {string}")
    fun `det gjores et kall til`(relativSti: String) {
        restTjeneste.exchangeGet(relativSti)
    }

    @Så("skal responsen inneholde json med property {string} og verdi {string}")
    fun `skal responsen inneholde json med property og verdi`(property: String, verdi: String) {
//        assertThat(restTjeneste.response).contains(""""$property":"$verdi"""")
    }

    @Så("skal aktuell klasse være {string}")
    fun `skal aktuell klasse vaere`(forventetEgenskap: String) {
        assertThat(this.javaClass.simpleName).isEqualTo(forventetEgenskap)
    }

    @Så("skal html responsen være {string}")
    fun `skal html responsen vaere`(httmlStatus: String) {
        // Write code here that turns the phrase above into concrete actions
        throw cucumber.api.PendingException()
    }
}
