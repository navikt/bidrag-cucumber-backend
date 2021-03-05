package no.nav.bidrag.cucumber.backend.dokument

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.backend.FellesEgenskaper
import no.nav.bidrag.cucumber.backend.FellesTestdataEgenskaper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertAll

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

    @Gitt("detaljer {string} = {string}")
    @Og("som har detaljer {string} = {string}")
    fun detaljer(key: String, value: String) {
        avvikData.leggTil(key, value)
    }

    @Og("saksnummer {string} for avviksbehandling av {string}")
    fun `saksnummer for avviksbehandling`(saksnummer: String, avvikstype: String) {
        avvikData = AvvikData(saksnummer = saksnummer)
        avvikData.avvikstype = avvikstype
        FellesTestdataEgenskaper.useAsKey(avvikstype)
    }

    @Og("saksnummer {string} for avviksbehandling for nøkkel {string}")
    fun `saksnummer for avviksbehandling for nokkel`(saksnummer: String, nokkel: String) {
        avvikData = AvvikData(saksnummer = saksnummer)
        FellesTestdataEgenskaper.useAsKey(nokkel)
    }

    @Og("avviksdato som brukes er saksnummer {string} og journalpostId {string}")
    fun `avviksdato som brukes er`(saksnummer: String, journalpostId: String) {
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

    @Når("jeg oppretter avvik på opprettet journalpost med nøkkel {string}")
    fun `jeg oppretter avvik pa opprettet journalpost`(nokkel: String) {
        restTjenesteAvvik().opprettAvvik(avvikData, nokkel)
    }

    @Når("jeg ber om gyldige avviksvalg for journalpost")
    fun `jeg ber om gyldige avviksvalg for journalpost`() {
        restTjenesteAvvik().exchangeGet(avvikData.lagEndepunktUrl())
    }

    @Når("jeg ber om gyldige avviksvalg for opprettet journalpost")
    @Og("når jeg ber om gyldige avviksvalg for opprettet journalpost")
    fun `jeg ber om gyldige avviksvalg for opprettet journalpost`() {
        restTjenesteAvvik().exchangeGet(avvikData.lagEndepunktUrlForHentAvvik())
    }

    @Når("jeg ber om gyldige avviksvalg for opprettet journalpost med nøkkel {string}")
    fun `jeg ber om gyldige avviksvalg for opprettet journalpost med nokkel`(nokkel: String) {
        restTjenesteAvvik().exchangeGet(avvikData.lagEndepunktUrlForHentAvvikFor(nokkel))
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

    @Og("listen med avvikstyper skal være tom")
    fun `listen med avvikstyper skal vare tom`() {
        val funnetAvvikstyper = restTjenesteAvvik().hentResponseSomListeAvStrenger()

        if (funnetAvvikstyper.isNotEmpty()) {
            assertThat(funnetAvvikstyper).size().isEqualTo(1)
            assertThat(funnetAvvikstyper).contains("")
        }
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

    @Gitt("jeg henter nylig opprettet journalpost av {string}")
    fun `jeg henter nylig opprettet journalpost`(avvikType: String) {
        restTjenesteAvvik().exchangeGet("/journal/${FellesTestdataEgenskaper.journalpostIdPerKey[avvikType]}?saksnummer=${avvikData.saksnummer}")
    }

    @Så("skal enhet være {string}")
    fun `skal enhet vare`(enhet: String) {
        val response = restTjenesteAvvik().hentResponseSomMap()
        @Suppress("UNCHECKED_CAST") val journalpost: LinkedHashMap<String, String> = response.get("journalpost") as LinkedHashMap<String, String>
        assertThat(journalpost.get("journalforendeEnhet")).isEqualTo(enhet)
    }

    @Og("når jeg jeg henter journalpost etter avviksbehandling")
    fun `nar jeg henter journalpost etter avvik`() {
        restTjenesteAvvik().exchangeGet("/journal/${avvikData.hentJournalpostId()}?saksnummer=${avvikData.saksnummer}")
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
