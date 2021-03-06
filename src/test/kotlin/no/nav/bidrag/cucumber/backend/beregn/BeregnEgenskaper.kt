package no.nav.bidrag.cucumber.backend.beregn

import com.jayway.jsonpath.JsonPath
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.backend.FellesEgenskaper
import org.assertj.core.api.Assertions.assertThat
import java.io.File

class BeregnEgenskaper {

  companion object {
    private const val BEREGN_PATH = "src/test/resources/no/nav/bidrag/cucumber/backend/beregn"

    fun projectPath(relativePath: String) = "$BEREGN_PATH/$relativePath"
  }

  @Når("jeg bruker endpoint {string} med json fra {string}")
  fun `nar jeg bruker endpoint med json fra fil`(endpoint: String, jsonFilePath: String) {
    val json = File(projectPath(jsonFilePath)).readText(Charsets.UTF_8)
    FellesEgenskaper.restTjeneste.exchangePost(endpoint, json)
  }

  @Og("responsen skal inneholde beløpet {string} under stien {string}")
  fun `responsen skal inneholde belop pa sti`(belop: String, sti: String) {
    val documentContext = JsonPath.parse(FellesEgenskaper.restTjeneste.hentResponse())
    var resultatBelop = documentContext.read<Any>(sti).toString()

    if (resultatBelop.endsWith(".0")) {
      resultatBelop = resultatBelop.removeSuffix(".0")
    }

    assertThat(resultatBelop).isEqualTo(belop)
  }

  @Og("responsen skal inneholde resultatkoden {string} under stien {string}")
  fun `responsen skal inneholde resultatkode pa sti`(resultatkode: String, sti: String) {
    val documentContext = JsonPath.parse(FellesEgenskaper.restTjeneste.hentResponse())
    val resultatKode = documentContext.read<String>(sti)

    assertThat(resultatKode).isEqualTo(resultatkode)
  }
}
