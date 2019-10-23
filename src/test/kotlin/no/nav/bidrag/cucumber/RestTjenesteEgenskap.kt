package no.nav.bidrag.cucumber

import com.fasterxml.jackson.databind.ObjectMapper
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Og
import io.cucumber.java.no.Så
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*
import kotlin.collections.ArrayList

class RestTjenesteEgenskap {
    companion object Manage {
        private lateinit var alias: String
        private lateinit var restTjeneste: RestTjeneste

        private fun hentEndpointUrl() = restTjeneste.endpointUrl
        private fun hentHttpStatus() = restTjeneste.httpStatus
        private fun hentResponse() = restTjeneste.response
    }

    @Gitt("resttjeneste {string}")
    fun `gitt resttjenste`(alias: String) {
        restTjeneste = RestTjeneste(alias)
        Manage.alias = alias
    }

    @Så("skal http status være {string}")
    fun `skal http status vaere`(enHttpStatus: String) {
        val httpStatus = HttpStatus.valueOf(enHttpStatus.toInt())

        assertThat(hentHttpStatus()).isEqualTo(httpStatus)
    }

    @Så("skal http status ikke være {string} eller {string}")
    fun `skal http status ikke vaere`(enHttpStatus: String, enAnnenHttpStatus: String) {
        val httpStatus = HttpStatus.valueOf(enHttpStatus.toInt())
        val annenHttpStatus = HttpStatus.valueOf(enAnnenHttpStatus.toInt())

        assertThat(hentHttpStatus()).`as`("HttpStatus for " + hentEndpointUrl()).isNotIn(EnumSet.of(httpStatus, annenHttpStatus))
    }

    @Og("resultatet er et objekt")
    fun `resultatet_vaere et objekt`() {
        assertThat(hentResponse()).isNotNull()
    }

    @Så("skal resultatet være en liste")
    fun `skal resultatet vaere en liste`() {
        assertThat(hentResponse()?.trim()).startsWith("[")
    }

    @Suppress("UNCHECKED_CAST")
    @Så("hvert element i listen skal ha {string} = {string}")
    fun `hvert element i listen skal ha`(key: String, value: String) {
        val verifyer = SoftAssertions()
        val responseObject = ObjectMapper().readValue(hentResponse(), List::class.java) as List<Map<String, Any>>

        responseObject.forEach {
            verifyer.assertThat(it.get(key)).`as`("id: ${it.get("journalpostId")}").isEqualTo(value)
        }

        verifyer.assertAll()
    }

    @Så("objektet skal ha {string} = {string}")
    fun objektet_skal_ha(key: String, value: String) {
        val responseObject = ObjectMapper().readValue(hentResponse(), Map::class.java)

        assertThat(responseObject.get("key")).isEqualTo(value)
    }

    @Så("objektet har følgende properties:")
    fun `objektet har folgende properties`(properties: List<String>) {
        val responseObject = ObjectMapper().readValue(hentResponse(), Map::class.java)
        val mangledeProps = ArrayList<String>()

        properties.forEach { if (!responseObject.containsKey(it)) mangledeProps.add(it) }

        assertThat(mangledeProps).`as`(hentResponse()).isEmpty()
    }

    @Suppress("UNCHECKED_CAST")
    @Så("{string} skal ha følgende properties:")
    fun `gitt object skal ha folgende properties`(obj: String, properties: List<String>) {
        val responseObject = ObjectMapper().readValue(hentResponse(), Map::class.java)
        val objects = responseObject[obj]
        val manglendeProperties: List<String>

        if (objects is List<*>) {
            manglendeProperties = hentManglendeProperties(objects, properties)
        } else if (objects is LinkedHashMap<*, *>) {
            manglendeProperties = hentManglendeProperties(objects, properties)
        } else {
            throw IllegalStateException("ukjennt type av $objects")
        }

        assertThat(manglendeProperties).`as`(hentResponse()).isEmpty()
    }

    private fun hentManglendeProperties(objects: List<*>, properties: List<String>): List<String> {
        val manglendeProps = ArrayList<String>()

        objects.forEach {
            @Suppress("UNCHECKED_CAST") manglendeProps.addAll(hentManglendeProperties(it as LinkedHashMap<String, *>, properties))
        }

        return manglendeProps

    }

    private fun hentManglendeProperties(objects: LinkedHashMap<*, *>, properties: List<String>): List<String> {
        val manglendeProps = ArrayList<String>()
        properties.forEach { if (!objects.containsKey(it)) manglendeProps.add(it) }

        return manglendeProps
    }

    fun exchangeGet(endpointUrl: String): ResponseEntity<String> {
        return restTjeneste.exchangeGet(endpointUrl)
    }

    fun response() = hentResponse()

    fun put(endpointUrl: String, journalpostJson: String) {
        restTjeneste.put(endpointUrl, journalpostJson)
    }
}
