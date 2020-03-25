package no.nav.bidrag.cucumber.dokument

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.FellesEgenskaper
import no.nav.bidrag.cucumber.FellesTestdataEgenskaper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertAll
import org.springframework.http.HttpStatus

class AvvikEgenskaper {
    companion object {
        lateinit var avvikData: AvvikData
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
        FellesTestdataEgenskaper.useAsKey(avvikstype)
    }

    @Og("endepunkt url lages av saksnummer {string} og journalpostId {string}")
    fun `endepunkt url er`(saksnummer: String, journalpostId: String) {
        avvikData = AvvikData(saksnummer = saksnummer, journalpostId = journalpostId)
    }

    @Og("enhetsnummer for avvik er {string}")
    fun `enhetsnummer for avvik er`(enhetsnummer: String) {
        avvikData.enhet = enhetsnummer
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
    @Og("når jeg ber om gyldige avviksvalg for opprettet journalpost")
    fun `jeg ber om gyldige avviksvalg for opprettet journalpost`() {
        restTjenesteAvvik().exchangeGet(avvikData.lagEndepunktUrlForAvvikstype())
    }

    @Og("listen med avvikstyper skal kun inneholde:")
    fun `listen med avvikstyper skal kun inneholde`(forventedeAvvik: List<String>) {
        val funnetAvvikstyper = restTjenesteAvvik().hentResponseSomListeAvStrenger()

        assertAll(
                { assertThat(funnetAvvikstyper).`as`("$funnetAvvikstyper vs $forventedeAvvik").hasSize(forventedeAvvik.size) },
                { assertThat(funnetAvvikstyper.contains("\"$forventedeAvvik\"")) }
        )
    }

    @Og("listen med avvikstyper skal inneholde {string}")
    fun `listen med avvikstyper skal inneholde`(avvikstype: String) {
        val funnetAvvikstyper = restTjenesteAvvik().hentResponseSomListeAvStrenger()

        assertThat(funnetAvvikstyper).contains("\"$avvikstype\"")
    }

    @Og("listen med avvikstyper skal ikke inneholde {string}")
    fun `listen med avvikstyper skal ikke inneholde`(avvikstype: String) {
        val funnetAvvikstyper = restTjenesteAvvik().hentResponseSomListeAvStrenger()

        assertThat(funnetAvvikstyper).doesNotContain("\"$avvikstype\"")
    }

    @Og("avvikstypen har beskrivelse {string}")
    fun avvikstypen_har_beskrivelse(beskrivelse: String) {
        avvikData.beskrivelse = beskrivelse
    }

    @Når("jeg henter journalposter for sak {string} med fagområde {string} for å sjekke avviksbehandling")
    fun `jeg henter journalposter for sak med fagomrade`(saksnummer: String, fagomrade: String) {
        restTjenesteAvvik().exchangeGet("/sak/$saksnummer/journal?fagomrade=$fagomrade")
    }

    @Når("jeg oppretter avvik med bekreftelse at den er sendt scanning")
    fun `jeg oppretter avvik med bekreftelse at den er sendt scanning`() {
        avvikData.leggTil("bekreftetSendtScanning", "true")
        restTjenesteAvvik().opprettAvvik(avvikData)
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

    @Og("når jeg jeg henter sakjournalen etter avviksbehandling")
    fun `nar jeg jeg henter sakjournalen etter avviksbehandling`() {
        restTjenesteAvvik().exchangeGet("/sak/${avvikData.saksnummer}/journal?fagomrade=BID")
    }

    @Og("sakjournalen skal inneholde journalposten med felt {string} = {string}")
    fun `sakjournalen skal inneholde journalposten med felt`(feltnavn: String, feltverdi: String) {
        val journalposter = restTjenesteAvvik().hentResponseSomListe()
        val journalpost = journalposter.find {
            val jpid = it["journalpostId"]
            val avvikJpid = avvikData.hentJournalpostId()

            avvikJpid == jpid
        }

        assertThat(journalpost).`as`("journalpst med id ${avvikData.hentJournalpostId()}").isNotNull
        if (journalpost != null) assertThat(journalpost[feltnavn]).`as`("journalpost er feilfort").isEqualTo(feltverdi.toBoolean())
    }

    private fun restTjenesteAvvik() = FellesEgenskaper.restTjeneste as RestTjenesteAvvik
}
