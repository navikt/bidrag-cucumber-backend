package no.nav.bidrag.cucumber.dokument

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.BidragCucumberScenarioManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertAll
import org.springframework.http.HttpStatus

class AvvikEgenskaper {
    companion object StepResources {
        lateinit var restTjenesteAvvik: RestTjenesteAvvik
        lateinit var avvikData: AvvikData
    }

    @Before
    fun `sett cucumber scenario`(scenario: Scenario) {
        BidragCucumberScenarioManager.use(scenario)
    }

    @Gitt("resttjenesten {string} for avviksbehandling")
    fun resttjenesten(alias: String) {
        restTjenesteAvvik = RestTjenesteAvvik(alias)
    }

    @Gitt("beskrivelsen {string}")
    fun beskrivelsen(beskrivelse: String) {
        avvikData.beskrivelse = beskrivelse
    }

    @Og("saksnummer {string} for avviksbehandling av {string}")
    fun `saksnummer for avviksbehandling`(saksnummer: String, avvikstype: String) {
        avvikData = AvvikData(saksnummer = saksnummer)
        avvikData.avvikstype = avvikstype
    }

    @Og("endepunkt url lages av saksnummer {string} og journalpostId {string}")
    fun `endepunkt url er`(saksnummer: String, journalpostId: String) {
        avvikData = AvvikData(saksnummer = saksnummer, journalpostId = journalpostId)
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
        restTjenesteAvvik.opprettAvvik(avvikData)
    }

    @Så("skal http status for avvik være {string}")
    fun `skal http status for avvik vaere`(enHttpStatus: String) {
        val httpStatus = HttpStatus.valueOf(enHttpStatus.toInt())

        assertThat(restTjenesteAvvik.hentHttpStatus()).`as`("HttpStatus for ${restTjenesteAvvik.hentEndpointUrl()}")
                .isEqualTo(httpStatus)
    }

    @Når("jeg ber om gyldige avviksvalg for journalpost")
    fun `jeg ber om gyldige avviksvalg for journalpost`() {
        restTjenesteAvvik.exchangeGet(avvikData.lagEndepunktUrl())
    }

    @Når("jeg ber om gyldige avviksvalg for opprettet journalpost")
    fun `jeg ber om gyldige avviksvalg for opprettet journalpost`() {
        restTjenesteAvvik.exchangeGet(avvikData.lagEndepunktUrlForAvvikstype())
    }

    @Så("skal http status for avviksbehandlingen være {string}")
    fun `skal http status for avviksbehandlingen vaere`(kode: String) {
        val httpStatus = HttpStatus.valueOf(kode.toInt())

        assertThat(restTjenesteAvvik.hentHttpStatus()).isEqualTo(httpStatus)
    }

    @Og("listen med valg skal kun inneholde:")
    fun `listen med valg skal kun inneholde`(forventedeAvvik: List<String>) {
        val funnetAvvikstyper = restTjenesteAvvik.hentResponseSomListeAvStrenger()

        assertAll(
                { assertThat(funnetAvvikstyper).`as`("$funnetAvvikstyper vs $forventedeAvvik").hasSize(forventedeAvvik.size) },
                { assertThat(funnetAvvikstyper.contains("\"$forventedeAvvik\"")) }
        )
    }

    @Og("listen med valg skal inneholde {string}")
    fun `listen med valg skal inneholde`(avvikstype: String) {
        val funnetAvvikstyper = restTjenesteAvvik.hentResponseSomListeAvStrenger()

        assertThat(funnetAvvikstyper).contains("\"$avvikstype\"")
    }

    @Og("listen med valg skal ikke inneholde {string}")
    fun `listen med valg skal ikke inneholde`(avvikstype: String) {
        val funnetAvvikstyper = restTjenesteAvvik.hentResponseSomListeAvStrenger()

        assertThat(funnetAvvikstyper).doesNotContain("\"$avvikstype\"")
    }

    @Og("avvikstypen har beskrivelse {string}")
    fun avvikstypen_har_beskrivelse(beskrivelse: String) {
        avvikData.beskrivelse = beskrivelse
    }

    @Og("opprett journalpost og ta vare på journalpostId:")
    fun `opprett journalpost og ta vare pa journalpostId`(jpJson: String) {
        if (avvikData.harIkkeJournalpostIdForAvvikstype()) {
            val restTjenesteTestdata = RestTjenesteTestdata()

            restTjenesteTestdata.opprettJournalpost(jpJson)
            assertThat(restTjenesteTestdata.hentHttpStatus()).isEqualTo(HttpStatus.CREATED)

            val opprettetJpMap = restTjenesteTestdata.hentResponseSomMap()
            avvikData.leggTilJournalpostIdForAvvikstype(opprettetJpMap["journalpostId"] as String)
        }
    }
}