package no.nav.bidrag.cucumber.dokument

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.BidragCucumberScenarioManager
import no.nav.bidrag.cucumber.RestTjeneste
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertAll
import org.springframework.http.HttpStatus

class AvvikEgenskap {
    companion object StepResources {
        lateinit var restTjenesteAvvik: RestTjenesteAvvik
        lateinit var restTjenesteForManipuleringAvDatabase: RestTjeneste
        lateinit var avvikData: AvvikData
    }

    @Before
    fun `sett cucumber scenario og initier resttjenester`(scenario: Scenario) {
        BidragCucumberScenarioManager.use(scenario)
        restTjenesteAvvik = RestTjenesteAvvik("bidragDokument")
        restTjenesteForManipuleringAvDatabase = RestTjeneste("bidragDokumentTestdata")
    }

    @Gitt("data på journalpost med id {string} inneholder:")
    fun `data pa journalpost med id inneholder`(journalpostId: String, json: String) {
        restTjenesteForManipuleringAvDatabase.put("/journalpost/$journalpostId", json)
    }

    @Og("endepunkt url er {string}")
    fun `endepunkt url er`(endepunktUrl: String) {
        avvikData = AvvikData(endepunktUrl)
    }

    @Og("enhetsnummer for avvik er {string}")
    fun `enhetsnummer for avvik er`(enhetsnummer: String) {
        avvikData.enhetsnummer = enhetsnummer

    }

    @Gitt("avvikstype {string}")
    fun avvikstype(avvikstype: String) {
        avvikData.avvikstype = avvikstype
    }

    @Når("jeg oppretter avvik")
    fun `jeg oppretter avvik`() {
        restTjenesteAvvik.post(avvikData)
    }

    @Så("skal http status for avvik være {string}")
    fun `skal http status for avvik vaere`(enHttpStatus: String) {
        val httpStatus = HttpStatus.valueOf(enHttpStatus.toInt())

        assertThat(restTjenesteAvvik.hentHttpStatus()).`as`("HttpStatus for ${restTjenesteAvvik.hentEndpointUrl()}")
                .isEqualTo(httpStatus)
    }

    @Når("jeg ber om gyldige avviksvalg for journalpost")
    fun `jeg ber om gyldige avviksvalg for journalpost`() {
        restTjenesteAvvik.exchangeGet(avvikData.endepunktUrl)
    }

    @Og("listen med valg skal kun inneholde:")
    fun `listen med valg skal kun inneholde`(forventedeAvvik: List<String>) {
        val funnetAvvikstyper = ArrayList(
                restTjenesteAvvik.hentResponse()!!
                        .removePrefix("[")
                        .removeSuffix("]")
                .split(",")
        )

        assertAll(
                { assertThat(forventedeAvvik).`as`("$forventedeAvvik vs $funnetAvvikstyper").hasSize(funnetAvvikstyper.size) },
                { forventedeAvvik.forEach{forventetAvvik -> assertThat(funnetAvvikstyper.contains("\"$forventedeAvvik\""))} }
        )
    }
}
