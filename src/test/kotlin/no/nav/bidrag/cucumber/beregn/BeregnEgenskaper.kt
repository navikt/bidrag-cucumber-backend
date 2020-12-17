package no.nav.bidrag.cucumber.beregn

import com.jayway.jsonpath.JsonPath
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.FellesEgenskaper
import org.assertj.core.api.Assertions.assertThat
import java.io.File

class BeregnEgenskaper {

  @Når("jeg bruker endpoint {string} med json fra {string}")
  fun `nar jeg bruker endpoint med json fra fil`(endpoint: String, jsonFilePath: String) {
    val json = File(jsonFilePath).readText(Charsets.UTF_8)
    FellesEgenskaper.restTjeneste.exchangePost(endpoint, json)
  }

  @Og("responsen skal inneholde beløpet {string} under stien {string}")
  fun `responsen skal inneholde belop pa sti`(belop: String, sti: String) {
    val documentContext = JsonPath.parse(FellesEgenskaper.restTjeneste.hentResponse())
    val resultatBelop = documentContext.read<Double>(sti)

    assertThat(resultatBelop).isEqualTo(belop.toDouble())
  }

  @Og("responsen skal inneholde heltallsbeløpet {string} under stien {string}")
  fun `responsen skal inneholde heltallsbelop pa sti`(belop: String, sti: String) {
    val documentContext = JsonPath.parse(FellesEgenskaper.restTjeneste.hentResponse())
    val resultatBelop = documentContext.read<Int>(sti)

    assertThat(resultatBelop).isEqualTo(belop.toInt())
  }

  @Og("responsen skal inneholde resultatkoden {string} under stien {string}")
  fun `responsen skal inneholde resultatkode pa sti`(resultatkode: String, sti: String) {
    val documentContext = JsonPath.parse(FellesEgenskaper.restTjeneste.hentResponse())
    val resultatKode = documentContext.read<String>(sti)

    assertThat(resultatKode).isEqualTo(resultatkode)
  }
}
