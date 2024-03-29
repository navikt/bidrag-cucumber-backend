package no.nav.bidrag.cucumber

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.commons.CorrelationId
import no.nav.bidrag.commons.ExceptionLogger
import no.nav.bidrag.cucumber.sikkerhet.Fasit
import no.nav.bidrag.cucumber.sikkerhet.Sikkerhet
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriTemplateHandler
import java.net.URI

@Suppress("UNCHECKED_CAST")
open class RestTjeneste(
    private val alias: String,
    private val rest: RestTemplateMedBaseUrl
) {
    private lateinit var debugFullUrl: String
    protected lateinit var responseEntity: ResponseEntity<String>

    constructor(applicationOrAlias: String) : this(applicationOrAlias, CacheRestTemplateMedBaseUrl.hentEllerKonfigurer(applicationOrAlias))
    constructor(alias: String, fasitRessurs: Fasit.FasitRessurs) : this(alias, CacheRestTemplateMedBaseUrl.hentEllerKonfigurer(alias, fasitRessurs))
    constructor(applicationOrAlias: String, applicationContext: String) : this(
        applicationOrAlias,
        CacheRestTemplateMedBaseUrl.hentEllerKonfigurer(applicationOrAlias, applicationContext)
    )

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RestTjeneste::class.java)

        internal fun initRestTemplate(url: String): RestTemplate {
            return setBaseUrlPa(RestTemplate(), url)
        }

        internal fun <T : RestTemplate> setBaseUrlPa(restTemplate: T, url: String): T {
            restTemplate.uriTemplateHandler = BaseUrlTemplateHandler(url)

            return restTemplate
        }
    }

    fun hentEndpointUrl() = debugFullUrl
    fun hentHttpHeaders(): HttpHeaders = responseEntity.headers
    fun hentHttpStatus(): HttpStatus = responseEntity.statusCode
    fun hentResponse(): String? = responseEntity.body
    fun hentResponseSomListe() = ObjectMapper().readValue(responseEntity.body, List::class.java) as List<Map<String, Any>>
    fun hentResponseSomMap() = try {
        ObjectMapper().readValue(responseEntity.body, Map::class.java) as Map<String, Any>
    } catch (e: Exception) {
        ExceptionLogger("bidrag-dokument-journalpost").logException(e, "responseEntity.body (${responseEntity.body})")
        throw e
    }

    fun exchangeGet(endpointUrl: String): ResponseEntity<String> {
        return exchangeGet(endpointUrl, null, "na")
    }

    fun exchangeGet(endpointUrl: String, username: String?, password: String): ResponseEntity<String> {
        debugFullUrl = rest.baseUrl + endpointUrl

        val header = initHttpHeadersWithCorrelationIdAndEnhet()

        if (username != null) {
            header.add(HttpHeaders.AUTHORIZATION, "Basic " + Sikkerhet.base64EncodeCredentials(username, password))
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
        headers.add(CorrelationId.CORRELATION_ID_HEADER, ScenarioManager.getCorrelationIdForScenario())
        headers.add(X_ENHET_HEADER, enhet ?: "4833")

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

    fun exchangePatch(endpointUrl: String, journalpostJson: String) {
        exchangePatch(endpointUrl, journalpostJson, null)
    }

    fun exchangePatch(endpointUrl: String, journalpostJson: String, enhet: String?) {
        val jsonEntity = httpEntity(endpointUrl, enhet, journalpostJson)
        exchange(jsonEntity, endpointUrl, HttpMethod.PATCH)
    }

    fun exchangePost(endpointUrl: String, json: String, enhet: String?) {
        val jsonEntity = httpEntity(endpointUrl, enhet, json)
        exchange(jsonEntity, endpointUrl, HttpMethod.POST)
    }

    fun exchangePost(endpointUrl: String, json: String) {
        val jsonEntity = httpEntity(endpointUrl = endpointUrl, json = json, enhet = null)
        exchange(jsonEntity, endpointUrl, HttpMethod.POST)
    }

    fun exchangePost(endpointUrl: String) {
        val jsonEntity = httpEntity(endpointUrl)
        exchange(jsonEntity, endpointUrl, HttpMethod.POST)
    }

    internal fun httpEntity(endpointUrl: String): HttpEntity<String> {
        this.debugFullUrl = rest.baseUrl + endpointUrl
        val headers = initHttpHeadersWithCorrelationIdAndEnhet()
        headers.contentType = MediaType.APPLICATION_JSON

        return HttpEntity(headers)
    }

    private fun httpEntity(endpointUrl: String, enhet: String?, json: String): HttpEntity<String> {
        this.debugFullUrl = rest.baseUrl + endpointUrl
        val headers = initHttpHeadersWithCorrelationIdAndEnhet(enhet)
        headers.contentType = MediaType.APPLICATION_JSON

        return HttpEntity(json, headers)
    }

    internal fun exchange(jsonEntity: HttpEntity<String>, endpointUrl: String, httpMethod: HttpMethod) {
        try {
            LOGGER.info("$httpMethod: $endpointUrl")
            responseEntity = rest.template.exchange(endpointUrl, httpMethod, jsonEntity, String::class.java)
        } catch (e: HttpStatusCodeException) {
            LOGGER.error("$httpMethod FEILET: $debugFullUrl: $e")
            responseEntity = ResponseEntity.status(e.statusCode).body<Any>("${e.javaClass.simpleName}: ${e.message}") as ResponseEntity<String>
            throw e
        }
    }

    fun post(endpointUrl: String, jsonEntity: HttpEntity<String>) {
        debugFullUrl = rest.baseUrl + endpointUrl

        responseEntity = try {
            rest.template.postForEntity(endpointUrl, jsonEntity, String::class.java)
        } catch (e: HttpStatusCodeException) {
            LOGGER.error("POST FEILET: $debugFullUrl: $e")
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

    private class BaseUrlTemplateHandler(val baseUrl: String) : UriTemplateHandler {
        override fun expand(uriTemplate: String, uriVariables: MutableMap<String, *>): URI {
            if (uriVariables.isNotEmpty()) {
                val queryString = StringBuilder()
                uriVariables.forEach { if (queryString.length == 1) queryString.append("$it") else queryString.append("?$it") }

                return URI.create(baseUrl + uriTemplate + queryString)
            }

            return URI.create(baseUrl + uriTemplate)
        }

        override fun expand(uriTemplate: String, vararg uriVariables: Any?): URI {
            if (uriVariables.isNotEmpty() && (uriVariables.size != 1 && uriVariables.first() != null)) {
                val queryString = StringBuilder("&")
                uriVariables.forEach {
                    if (it != null && queryString.length == 1) {
                        queryString.append("$it")
                    } else if (it != null) {
                        queryString.append("?$it")
                    }
                }

                return URI.create(baseUrl + uriTemplate + queryString)
            }

            return URI.create(baseUrl + uriTemplate)
        }
    }

    class RestTemplateMedBaseUrl(val template: RestTemplate, val baseUrl: String)
}
