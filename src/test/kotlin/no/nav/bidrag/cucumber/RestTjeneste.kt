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
import org.springframework.web.client.RestTemplate


@Suppress("UNCHECKED_CAST")
open class RestTjeneste(
        private val alias: String,
        private val rest: RestTemplateMedBaseUrl,
        private val sikkerhet: Sikkerhet = Sikkerhet()
) {

    private lateinit var debugFullUrl: String
    protected lateinit var responseEntity: ResponseEntity<String>

    constructor(applicationOrAlias: String) : this(applicationOrAlias, CacheRestTemplateMedBaseUrl().hentEllerKonfigurer(applicationOrAlias))
    constructor(applicationOrAlias: String, applicationContext: String) : this(applicationOrAlias, CacheRestTemplateMedBaseUrl().hentEllerKonfigurer(applicationOrAlias, applicationContext))

    fun hentEndpointUrl() = debugFullUrl
    fun hentHttpHeaders(): HttpHeaders = responseEntity.headers
    fun hentHttpStatus(): HttpStatus = responseEntity.statusCode
    fun hentResponse(): String? = responseEntity.body
    fun hentResponseSomListe() = ObjectMapper().readValue(responseEntity.body, List::class.java) as List<Map<String, Any>>
    fun hentResponseSomMap() = ObjectMapper().readValue(responseEntity.body, Map::class.java) as Map<String, Any>

    fun exchangeGet(endpointUrl: String): ResponseEntity<String> {
        return exchangeGet(endpointUrl, null, "na")
    }

    fun exchangeGet(endpointUrl: String, username: String?, password: String): ResponseEntity<String> {
        debugFullUrl = rest.baseUrl + endpointUrl

        val header = initHttpHeadersWithCorrelationIdAndEnhet()

        if (username != null) {
            header.add(HttpHeaders.AUTHORIZATION, "Basic " + sikkerhet.base64EncodeCredentials(username, password))
        }

        ScenarioManager.log("GET ${this.debugFullUrl}")

        responseEntity = try {
            rest.template.exchange(endpointUrl, HttpMethod.GET, HttpEntity(null, header), String::class.java)
        } catch (e: HttpStatusCodeException) {
            ResponseEntity(headerWithAlias(), e.statusCode)
        }

        ScenarioManager.log(
                if (responseEntity.body != null) "response with json and status ${responseEntity.statusCode}"
                else "no response body with status ${responseEntity.statusCode}"
        )

        return responseEntity
    }

    internal fun initHttpHeadersWithCorrelationIdAndEnhet(): HttpHeaders {
        return initHttpHeadersWithCorrelationIdAndEnhet(null)
    }

    private fun initHttpHeadersWithCorrelationIdAndEnhet(enhet: String?): HttpHeaders {
        val headers = HttpHeaders()
        headers.add(CorrelationId.CORRELATION_ID_HEADER, ScenarioManager.fetchCorrelationIdForScenario())
        headers.add(X_ENHET_HEADER, enhet ?: "4802")

        ScenarioManager.log(
                ScenarioManager.createCorrelationIdLinkTitle(),
                ScenarioManager.createQueryLinkForCorrelationId()
        )

        return headers
    }

    private fun headerWithAlias(): MultiValueMap<String, String> {
        val httpHeaders = HttpHeaders()
        httpHeaders.add("ERROR_REST_SERVICE", alias)

        return httpHeaders
    }

    fun exchangePut(endpointUrl: String, journalpostJson: String) {
        exchangePut(endpointUrl, journalpostJson, null)
    }

    fun exchangePut(endpointUrl: String, journalpostJson: String, enhet: String?) {
        val jsonEntity = httpEntity(endpointUrl, enhet, journalpostJson)
        exchange(jsonEntity, endpointUrl, HttpMethod.PUT)
    }

    fun exchangePost(endpointUrl: String, journalpostJson: String, enhet: String?) {
        val jsonEntity = httpEntity(endpointUrl, enhet, journalpostJson)
        exchange(jsonEntity, endpointUrl, HttpMethod.POST)
    }

    fun exchangePost(endpointUrl: String) {
        val jsonEntity = httpEntity(endpointUrl)
        exchange(jsonEntity, endpointUrl, HttpMethod.POST)
    }

    private fun httpEntity(endpointUrl: String): HttpEntity<String> {
        this.debugFullUrl = rest.baseUrl + endpointUrl
        val headers = initHttpHeadersWithCorrelationIdAndEnhet()
        headers.contentType = MediaType.APPLICATION_JSON

        return HttpEntity(headers)
    }

    private fun httpEntity(endpointUrl: String, enhet: String?, journalpostJson: String): HttpEntity<String> {
        this.debugFullUrl = rest.baseUrl + endpointUrl
        val headers = initHttpHeadersWithCorrelationIdAndEnhet(enhet)
        headers.contentType = MediaType.APPLICATION_JSON

        return HttpEntity(journalpostJson, headers)
    }

    private fun exchange(jsonEntity: HttpEntity<String>, endpointUrl: String, httpMethod: HttpMethod) {
        try {
            println(jsonEntity)
            responseEntity = rest.template.exchange(endpointUrl, httpMethod, jsonEntity, String::class.java)
        } catch (e: HttpStatusCodeException) {
            System.err.println("$httpMethod FEILET: ${this.debugFullUrl}: $e")
            responseEntity = ResponseEntity.status(e.statusCode).body<Any>(e.message) as ResponseEntity<String>
            throw e
        }
    }

    fun post(endpointUrl: String, jsonEntity: HttpEntity<String>) {
        debugFullUrl = rest.baseUrl + endpointUrl

        responseEntity = try {
            rest.template.postForEntity(endpointUrl, jsonEntity, String::class.java)
        } catch (e: HttpStatusCodeException) {
            System.err.println("POST FEILET: ${this.debugFullUrl}: $e")
            ResponseEntity(e.statusCode)

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

    class RestTemplateMedBaseUrl(val template: RestTemplate, val baseUrl: String)
}
