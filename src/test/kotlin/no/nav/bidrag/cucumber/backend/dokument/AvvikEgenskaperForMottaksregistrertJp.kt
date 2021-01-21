package no.nav.bidrag.cucumber.backend.dokument

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.backend.FellesEgenskaper
import no.nav.bidrag.cucumber.backend.FellesTestdataEgenskaper
import org.assertj.core.api.Assertions.assertThat

class AvvikEgenskaperForMottaksregistrertJp {

    companion object {
        lateinit var avvikData: AvvikDataMottaksregistrertJp
    }

    @Gitt("resttjenesten {string} for å finne avvik på mottaksredigert journalpost, avvikstype {string}")
    fun `resttjeneste og finn avvikstype`(alias: String, avvikstype: String) {
        FellesEgenskaper.restTjeneste = RestTjenesteAvvik(alias)
        avvikData = AvvikDataMottaksregistrertJp(avvikstype, "$alias-finn-$avvikstype")
    }

    @Gitt("resttjenesten {string} for å registrere avvik på mottaksredigert journalpost, avvikstype {string}")
    fun `resttjeneste og registrer avvikstype`(alias: String, avvikstype: String) {
        FellesEgenskaper.restTjeneste = RestTjenesteAvvik(alias)
        avvikData = AvvikDataMottaksregistrertJp(avvikstype, "$alias-registrer-$avvikstype")
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

    @Og("så skal listen med avvikstyper ikke inneholde {string}")
    fun `listen med avvikstyper skal ikke inneholde`(avvikstype: String) {
        val funnetAvvikstyper = restTjenesteAvvik().hentResponseSomListeAvStrenger()

        assertThat(funnetAvvikstyper).doesNotContain("\"$avvikstype\"")
    }

    @Og("resttjenesten {string} til å opprette journalpost når den ikke finnes for avvikstypen:")
    fun `bruk resttjeneste opprett journalpost`(resttjenesteTestdata: String, jpJson: String) {
        val testdataEgenskaper = FellesTestdataEgenskaper()

        testdataEgenskaper.resttjenesten(resttjenesteTestdata)
        testdataEgenskaper.`opprett journalpost`(avvikData, jpJson)
    }

    private fun restTjenesteAvvik() = FellesEgenskaper.restTjeneste as RestTjenesteAvvik

    @Gitt("enhet for behandling av avvik på mottaksregistrert journalpost er {string}")
    fun `enhet for behandling av avvik pa mottaksregistrert journalpost er`(enhetsnummer: String) {
        avvikData.enhet = enhetsnummer
    }

    @Når("jeg registrerer avviket med url {string}:")
    fun `jeg registrerer avviket med url`(path: String, journalpostJson: String) {
        val endpointUrl = avvikData.lagEndepunktUrl(path)
        restTjenesteAvvik().exchangePost(endpointUrl, journalpostJson, avvikData.enhet)
    }

    @Og("når jeg jeg henter journalpost etter avviksbehandling med url {string}")
    fun `nar jeg henter journalpost etter avvik`(path: String) {
        restTjenesteAvvik().exchangeGet(avvikData.lagEndepunktUrl(path))
    }
}
