package no.nav.bidrag.cucumber.beregn

import com.jayway.jsonpath.JsonPath
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.FellesEgenskaper
import org.assertj.core.api.Assertions.assertThat
import java.io.File

class BeregnEgenskaper {

    @Når("jeg bruker endpoint {string} med json fra fil")
    fun `nar jeg bruker endpoint med json fra fil`(endpoint: String) {
        val json = lesFraFil()
        FellesEgenskaper.restTjeneste.exchangePost(endpoint, json)
    }

    private fun lesFraFil(): String {
        val filnavn = "src/test/resources/no/nav/bidrag/cucumber/beregn/barnebidrag/barnebidrag_eksempel1.txt"
        return File(filnavn).readText(Charsets.UTF_8)
    }

    @Og("responsen skal inneholde beløpet {int} under stien {string}")
    fun `responsen skal inneholde belop pa sti`(belop: Double, sti: String) {
        val documentContext = JsonPath.parse(FellesEgenskaper.restTjeneste.hentResponse())
        val x = documentContext.read<Double>(sti)

        assertThat(x).isEqualTo(belop)
    }

    @Og("responsen skal inneholde en streng {string} under stien {string}")
    fun `responsen skal inneholde streng pa sti`(streng: String, sti: String) {
        val documentContext = JsonPath.parse(FellesEgenskaper.restTjeneste.hentResponse())
        val x = documentContext.read<String>(sti)

        assertThat(x).isEqualTo(streng)
    }
}