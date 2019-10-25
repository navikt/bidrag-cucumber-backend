package no.nav.bidrag.cucumber

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.commons.CorrelationId
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpStatusCodeException
import java.util.LinkedHashMap

@Suppress("UNCHECKED_CAST")
open class RestTjeneste(
        private val alias: String,
        private val rest: RestTemplateMedBaseUrl,
        private var endpointUrl: String = alias,
        private var response: String? = null

) : BidragCucumberScenarioManager() {

    private lateinit var httpStatus: HttpStatus

    constructor(alias: String) : this(alias, Fasit().hentRestTemplateFor(alias))

    fun hentEndpointUrl() = endpointUrl
    fun hentHttpStatus() = httpStatus
    fun hentResponse() = response
    fun hentResponseSomListe() = ObjectMapper().readValue(response, List::class.java) as List<Map<String, Any>>
    fun hentResponseSomMap() = ObjectMapper().readValue(response, Map::class.java) as Map<String, Any>

    fun exchangeGet(endpointUrl: String): ResponseEntity<String> {
        this.endpointUrl = rest.baseUrl + endpointUrl
        val header = httpHeadersWithCorrelationId()

        writeToCucumberScenario("GET ${this.endpointUrl}")

        val stringEntity: ResponseEntity<String> = try {
            rest.template.exchange(endpointUrl, HttpMethod.GET, HttpEntity(null, header), String::class.java)
        } catch (e: HttpStatusCodeException) {
            ResponseEntity(headerWithAlias(), e.statusCode)
        }

        response = stringEntity.body
        httpStatus = stringEntity.statusCode

        writeToCucumberScenario("$httpStatus$ADD_LINEFEED$response")

        return stringEntity
    }

    private fun httpHeadersWithCorrelationId(): HttpHeaders {
        val headers = HttpHeaders()
        headers.add(CorrelationId.CORRELATION_ID_HEADER, correlationIdForScenario)

        writeOnceToCucumberScenario(
                ScenarioMessage.CORELATION_ID,
                "Link til kibana for correlation-id: $correlationIdForScenario. Gjelder for scenario: '$scenarioName'$ADD_LINEFEED$ADD_LINEFEED" +
                        "https://logs.adeo.no/app/kibana#/discover?_g=()&_a=(columns:!(message,envclass,environment,level,application,host),index:'96e648c0-980a-11e9-830a-e17bbd64b4db',interval:auto,query:(language:lucene,query:\"$correlationIdForScenario\"),sort:!('@timestamp',desc))\n"
        )

        return headers
    }

    private fun headerWithAlias(): MultiValueMap<String, String> {
        val httpHeaders = HttpHeaders()
        httpHeaders.add("ERROR_REST_SERVICE", alias)

        return httpHeaders
    }

    fun put(endpointUrl: String, journalpostJson: String) {
        this.endpointUrl = rest.baseUrl + endpointUrl
        val headers = httpHeadersWithCorrelationId()
        headers.contentType = MediaType.APPLICATION_JSON

        val jsonEntity = HttpEntity(journalpostJson, headers)

        try {
            println(jsonEntity)
            httpStatus = rest.template.exchange(endpointUrl, HttpMethod.PUT, jsonEntity, String::class.java).statusCode
        } catch (e: HttpStatusCodeException) {
            System.err.println("OPPDATERING FEILET: ${this.endpointUrl}: $e")
            throw e
        }
    }

    fun hentManglendeProperties(objects: List<*>, properties: List<String>): List<String> {
        val manglendeProps = ArrayList<String>()

        objects.forEach {
            @Suppress("UNCHECKED_CAST") manglendeProps.addAll(hentManglendeProperties(it as LinkedHashMap<String, *>, properties))
        }

        return manglendeProps
    }

    fun hentManglendeProperties(objects: LinkedHashMap<*, *>, properties: List<String>): List<String> {
        val manglendeProps = ArrayList<String>()
        properties.forEach { if (!objects.containsKey(it)) manglendeProps.add(it) }

        return manglendeProps
    }
}
