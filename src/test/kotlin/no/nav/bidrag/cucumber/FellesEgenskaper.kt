package no.nav.bidrag.cucumber

import io.cucumber.java.Before
import io.cucumber.java.Scenario
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.springframework.http.HttpStatus

class FellesEgenskaper {
    companion object {
        lateinit var restTjeneste: RestTjeneste
    }

    @Before
    fun `manage scenario`(scenario: Scenario) {
        ScenarioManager.use(scenario)
    }

    @Gitt("resttjenesten {string}")
    fun resttjenesten(alias: String) {
        restTjeneste = RestTjeneste(alias)
    }

    @Gitt("nais applikasjon {string}")
    fun gittNaisApp(naisApp: String) {
        restTjeneste = RestTjeneste(naisApp)
    }

    @Så("skal http status være {string}")
    fun `skal http status vaere`(enHttpStatus: String) {
        val httpStatus = HttpStatus.valueOf(enHttpStatus.toInt())

        assertThat(restTjeneste.hentHttpStatus()).`as`("HttpStatus for ${restTjeneste.hentEndpointUrl()}")
                .isEqualTo(httpStatus)
    }

    @Og("så skal responsen være et objekt")
    fun `skal responsen vaere et objekt`() {
        assertThat(restTjeneste.hentResponse()?.trim()).startsWith("{").isNotEqualTo("{}")
    }

    @Og("responsen skal inneholde {string} = {string}")
    @Så("skal responsen inneholde {string} = {string}")
    fun `responsen skal inneholde`(key: String, value: String) {
        val responseObject = restTjeneste.hentResponseSomMap()
        val verdiFraResponse = responseObject[key]?.toString()

        assertThat(verdiFraResponse).`as`("json response").isEqualTo(value)
    }

    @Og("responsen skal inneholde et objekt med navn {string} som har feltet {string} = {string}")
    @Så("skal responsen inneholde et objekt med navn {string} som har feltet {string} = {string}")
    fun `responsen skal inneholde et objekt med feltet`(objekt: String, key: String, value: String) {
        val responseObject = restTjeneste.hentResponseSomMap()
        @Suppress("UNCHECKED_CAST") val objektFraResponse = responseObject[objekt] as Map<String, Any>?
        val verdiFraResponse = objektFraResponse?.get(key)?.toString()

        assertThat(verdiFraResponse).`as`("$objekt skal inneholde $key").isEqualTo(value)
    }

    @Og("responsen skal inneholde et objekt med navn {string} som har feltet {string} uten verdi {string}")
    fun `responsen skal inneholde et objekt med feltet uten gitt verdi`(objekt: String, key: String, value: String) {
        val responseObject = restTjeneste.hentResponseSomMap()
        @Suppress("UNCHECKED_CAST") val objektFraResponse = responseObject[objekt] as Map<String, Any>?
        val verdiFraResponse = objektFraResponse?.get(key)?.toString()

        assertThat(objektFraResponse).`as`("$objekt skal inneholde $key").containsKey(key)
        assertThat(verdiFraResponse).`as`("$key skal ikke være $value").isNotEqualTo(value)

    }

    @Og("responsen skal inneholde et objekt med navn {string} som har feltene:")
    fun `responsen skal inneholde et objekt som har feltene`(objekt: String, felter: List<String>) {
        val responseObject = restTjeneste.hentResponseSomMap()
        val journalpostMap = responseObject[objekt] as Map<*, *>?
        val manglerFelt = ArrayList<String>()

        felter.forEach { if (!journalpostMap?.containsKey(it)!!) manglerFelt.add(it) }

        assertThat(manglerFelt).`as`("Response skal ikke mangle noen av $felter").isEmpty()
    }

    @Og("responsen skal inneholde et objekt med navn {string} som har et felt {string} med feltet {string}")
    fun `responsen skal inneholde et objekt med felt som har felt`(objekt: String, objektFelt: String, felt: String) {
        val journalpostResponse = restTjeneste.hentResponseSomMap()
        val journalpostMap = journalpostResponse[objekt] as Map<*, *>?
        @Suppress("UNCHECKED_CAST") val feltMap = journalpostMap?.get(objektFelt) as Map<String, *>?

        assertThat(feltMap).`as`("Response skal inneholde feltet $felt").containsKey(felt)
    }

    @Og("responsen skal inneholde et objekt med navn {string} som har et felt {string} med feltene:")
    fun `responsen skal inneholde et objekt med felt med feltene`(objekt: String, objektFelt: String, forventedeFelter: List<String>) {
        val journalpostResponse = restTjeneste.hentResponseSomMap()
        val journalpostMap = journalpostResponse[objekt] as Map<*, *>?
        @Suppress("UNCHECKED_CAST") val reelleFelter = (journalpostMap?.get(objektFelt) as List<Map<*, *>>?)?.first()

        assertThat(reelleFelter).isNotNull
        val manglerFelt = ArrayList<String>()
        forventedeFelter.forEach { if (!reelleFelter!!.containsKey(it)) manglerFelt.add(it) }

        assertThat(manglerFelt).`as`("Response med $objektFelt skal ikke mangle noen av $forventedeFelter").isEmpty()
    }

    @Og("responsen skal ikke inneholde {string} = {string}")
    fun `responsen skal ikke inneholde`(key: String, value: String) {
        val responseObject = restTjeneste.hentResponseSomMap()
        val verdiFraResponse = responseObject[key]?.toString()

        assertThat(verdiFraResponse).`as`("json response skal inneholde $key").isNotEqualTo(value)
    }

    @Suppress("UNCHECKED_CAST")
    @Så("hvert element i listen skal ha {string} = {string}")
    fun `hvert element i listen skal ha`(key: String, value: String) {
        val verifyer = SoftAssertions()
        val responseObject = restTjeneste.hentResponseSomListe()

        responseObject.forEach {
            verifyer.assertThat(it[key]).`as`("id: ${it["journalpostId"]}").isEqualTo(value)
        }

        verifyer.assertAll()
    }

    @Så("objektet har følgende properties:")
    fun `objektet har folgende properties`(properties: List<String>) {
        val responseObject = restTjeneste.hentResponseSomMap()
        val mangledeProps = ArrayList<String>()

        properties.forEach { if (!responseObject.containsKey(it)) mangledeProps.add(it) }

        assertThat(mangledeProps).`as`("Responsen skal ha alle properties: $properties").isEmpty()
    }

    @Suppress("UNCHECKED_CAST")
    @Så("{string} skal ha følgende properties:")
    fun `gitt object skal ha folgende properties`(obj: String, properties: List<String>) {
        val responseObject = restTjeneste.hentResponseSomMap()
        val manglendeProperties: List<String> = when (val objects = responseObject[obj]) {
            is List<*> -> restTjeneste.hentManglendeProperties(objects, properties)
            is LinkedHashMap<*, *> -> restTjeneste.hentManglendeProperties(objects, properties)
            else -> throw IllegalStateException("ukjennt type av $objects")
        }

        assertThat(manglendeProperties).`as`("$obj skal ikke mangle noen av $properties i response")
                .isEmpty()
    }

    @Og("så skal responsen være en liste")
    fun `skal responsen vaere en liste`() {
        assertThat(restTjeneste.hentResponse()?.trim()).startsWith("[")
    }

    @Og("hvert element i listen skal ha følgende properties satt:")
    fun `hvert element i listen skal ha folgende properties satt`(properties: List<String>) {
        val verifyer = SoftAssertions()
        val responseObject = restTjeneste.hentResponseSomListe()

        responseObject.forEach { element ->
            properties.forEach { verifyer.assertThat(element).`as`("missing $it in jp: ${element["journalpostId"]})").containsKey(it) }
        }

        verifyer.assertAll()
    }

    @Når("jeg kaller endpoint {string} med parameter {string} = {string}")
    fun `jeg kaller endpoint med parameter`(endpoint: String, parameterNavn: String, parameterVerdi: String) {
        restTjeneste.exchangeGet("$endpoint?$parameterNavn=$parameterVerdi")
    }
}
