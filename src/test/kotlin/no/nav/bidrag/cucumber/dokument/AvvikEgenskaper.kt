package no.nav.bidrag.cucumber.dokument

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.ScenarioManager
import no.nav.bidrag.cucumber.FellesEgenskaper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertAll
import org.springframework.http.HttpStatus

class AvvikEgenskaper {
    companion object {
        lateinit var avvikData: AvvikData
    }

    @Before
    fun `manage scenario`(scenario: Scenario) {
        ScenarioManager.use(scenario)
    }

    @Gitt("resttjenesten {string} for avviksbehandling")
    fun resttjenesten(alias: String) {
        FellesEgenskaper.restTjeneste = RestTjenesteAvvik(alias)
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
        restTjenesteAvvik().opprettAvvik(avvikData)
    }

    @Når("jeg ber om gyldige avviksvalg for journalpost")
    fun `jeg ber om gyldige avviksvalg for journalpost`() {
        restTjenesteAvvik().exchangeGet(avvikData.lagEndepunktUrl())
    }

    @Når("jeg ber om gyldige avviksvalg for opprettet journalpost")
    fun `jeg ber om gyldige avviksvalg for opprettet journalpost`() {
        restTjenesteAvvik().exchangeGet(avvikData.lagEndepunktUrlForAvvikstype())
    }

    @Og("listen med valg skal kun inneholde:")
    fun `listen med valg skal kun inneholde`(forventedeAvvik: List<String>) {
        val funnetAvvikstyper = restTjenesteAvvik().hentResponseSomListeAvStrenger()

        assertAll(
                { assertThat(funnetAvvikstyper).`as`("$funnetAvvikstyper vs $forventedeAvvik").hasSize(forventedeAvvik.size) },
                { assertThat(funnetAvvikstyper.contains("\"$forventedeAvvik\"")) }
        )
    }

    @Og("listen med valg skal inneholde {string}")
    fun `listen med valg skal inneholde`(avvikstype: String) {
        val funnetAvvikstyper = restTjenesteAvvik().hentResponseSomListeAvStrenger()

        assertThat(funnetAvvikstyper).contains("\"$avvikstype\"")
    }

    @Og("listen med valg skal ikke inneholde {string}")
    fun `listen med valg skal ikke inneholde`(avvikstype: String) {
        val funnetAvvikstyper = restTjenesteAvvik().hentResponseSomListeAvStrenger()

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

    @Når("jeg henter journalposter for sak {string} med fagområde {string} for å sjekke avviksbehandling")
    fun `jeg henter journalposter for sak med fagomrade`(saksnummer: String, fagomrade: String) {
        restTjenesteAvvik().exchangeGet("/sakjournal/$saksnummer?fagomrade=$fagomrade")
    }

    @Og("listen med journalposter skal ikke inneholde id for journalposten")
    fun `listen med journalposter skal ikke inneholde id for journalposten`() {
        val journalpostMapSomListe = restTjenesteAvvik().hentResponseSomListe()
        val listeMedAlleJournalpostId = journalpostMapSomListe.map { it["journalpostId"].toString() }
        val listeMedGenerertJournalpostId = listeMedAlleJournalpostId.filter { avvikData.erForJournalpostId(it) }

        assertThat(listeMedGenerertJournalpostId).`as`("filtrert liste fra " + listeMedAlleJournalpostId).isEmpty()
    }

    @Og("når jeg jeg henter journalpost etter avviksbehandling")
    fun `nar jeg henter journalpost etter avvik`() {
        restTjenesteAvvik().exchangeGet("/sak/${avvikData.saksnummer}/journal/${avvikData.hentJournalpostId()}")
    }
    
    private fun restTjenesteAvvik() = FellesEgenskaper.restTjeneste as RestTjenesteAvvik
}
