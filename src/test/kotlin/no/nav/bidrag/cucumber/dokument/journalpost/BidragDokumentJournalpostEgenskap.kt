package no.nav.bidrag.cucumber.dokument.journalpost

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.BidragCucumberScenarioManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.springframework.http.HttpStatus

class BidragDokumentJournalpostEgenskap {

    companion object {
        private lateinit var restTjenesteJournalpost: RestTjenesteJournalpost
    }

    @Before
    fun `sett cucumber scenario`(scenario: Scenario) {
        BidragCucumberScenarioManager.use(scenario)
    }

    @Gitt("resttjenesten bidragDokumentJournalpost")
    fun `gitt resttjenesten bidragDokumenJournalpost`() {
        restTjenesteJournalpost = RestTjenesteJournalpost()
    }

    @Gitt("jeg henter journalpost for sak {string} med id {string}")
    fun `jeg henter journalpost for sak med id`(saksnummer: String, journalpostId: String) {
        restTjenesteJournalpost.exchangeGet("/sak/$saksnummer/journal/$journalpostId")
    }

    @Gitt("jeg henter journalposter for sak {string} med fagområde {string}")
    fun `jeg henter journalposter for sak med fagomrade`(saksnummer: String, fagomrade: String) {
        restTjenesteJournalpost.exchangeGet("/sakjournal/$saksnummer?fagomrade=$fagomrade")
    }

    @Gitt("jeg endrer journalpost for sak {string} med id {string} til:")
    fun `jeg endrer journalpost med id til`(saksnummer: String, journalpostId: String, journalpostJson: String) {
        restTjenesteJournalpost.put("/sak/$saksnummer/journal/$journalpostId", journalpostJson)
    }

    @Og("resultatet er et objekt")
    fun `resultatet_vaere et objekt`() {
        assertThat(restTjenesteJournalpost.hentResponse()).isNotNull()
    }

    @Så("skal resultatet være en liste")
    fun `skal resultatet vaere en liste`() {
        assertThat(restTjenesteJournalpost.hentResponse()?.trim()).startsWith("[")
    }

    @Suppress("UNCHECKED_CAST")
    @Så("hvert element i listen skal ha {string} = {string}")
    fun `hvert element i listen skal ha`(key: String, value: String) {
        val verifyer = SoftAssertions()
        val responseObject = restTjenesteJournalpost.hentResponseSomListe()

        responseObject.forEach {
            verifyer.assertThat(it.get(key)).`as`("id: ${it.get("journalpostId")}").isEqualTo(value)
        }

        verifyer.assertAll()
    }

    @Så("objektet skal ha {string} = {string}")
    fun objektet_skal_ha(key: String, value: String) {
        val responseObject = restTjenesteJournalpost.hentResponseSomMap()

        assertThat(responseObject[key]).`as`("json response (${restTjenesteJournalpost.hentResponse()})").isEqualTo(value)
    }

    @Så("objektet har følgende properties:")
    fun `objektet har folgende properties`(properties: List<String>) {
        val responseObject = restTjenesteJournalpost.hentResponseSomMap()
        val mangledeProps = ArrayList<String>()

        properties.forEach { if (!responseObject.containsKey(it)) mangledeProps.add(it) }

        assertThat(mangledeProps).`as`("${restTjenesteJournalpost.hentResponse()} skal ikke mangle noen av $properties").isEmpty()
    }

    @Suppress("UNCHECKED_CAST")
    @Så("{string} skal ha følgende properties:")
    fun `gitt object skal ha folgende properties`(obj: String, properties: List<String>) {
        val responseObject = restTjenesteJournalpost.hentResponseSomMap()
        val objects = responseObject[obj]
        val manglendeProperties: List<String>

        if (objects is List<*>) {
            manglendeProperties = restTjenesteJournalpost.hentManglendeProperties(objects, properties)
        } else if (objects is LinkedHashMap<*, *>) {
            manglendeProperties = restTjenesteJournalpost.hentManglendeProperties(objects, properties)
        } else {
            throw IllegalStateException("ukjennt type av $objects")
        }

        assertThat(manglendeProperties).`as`("$obj skal ikke mangle noen av $properties: ${restTjenesteJournalpost.hentResponse()}")
                .isEmpty()
    }
}
