package no.nav.bidrag.cucumber.dokument

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.FellesEgenskaper
import no.nav.bidrag.cucumber.FellesTestdataEgenskaper
import org.assertj.core.api.Assertions.assertThat

class AvvikEgenskaperForMottaksregistrertJp {

    companion object {
        lateinit var avvikData: AvvikDataMottaksregistrertJp
    }

    @Gitt("resttjenesten {string} for avviksbehandling av mottaksredigert journalpost for avvikstype {string}")
    fun `resttjeneste og avvikstype`(alias: String, avvikstype: String) {
        FellesEgenskaper.restTjeneste = RestTjenesteAvvik(alias)
        avvikData = AvvikDataMottaksregistrertJp(avvikstype, alias + avvikstype)
    }

    @Når("jeg skal finne avvik med path {string}")
    fun `jeg skal finne avvik med path`(path: String) {
        val endpointUrl = avvikData.lagEndepunktUrl(path)
        restTjenesteAvvik().exchangeGet(endpointUrl)
    }

    @Så("skal listen med avvikstyper inneholde {string}")
    fun `listen med avvikstyper skal inneholde`(avvikstype: String) {
        val funnetAvvikstyper = restTjenesteAvvik().hentResponseSomListeAvStrenger()

        assertThat(funnetAvvikstyper).contains("\"$avvikstype\"")
    }

    @Og("resttjenesten {string} til å opprette journalpost når den ikke finnes for avvikstype")
    fun `bruk resttjeneste opprett journalpost`(resttjenesteTestdata: String, jpJson: String) {
        val testdataEgenskaper = FellesTestdataEgenskaper()

        testdataEgenskaper.resttjenesten(resttjenesteTestdata)
        testdataEgenskaper.`opprett journalpost`(avvikData, jpJson)
    }

    private fun restTjenesteAvvik() = FellesEgenskaper.restTjeneste as RestTjenesteAvvik
}