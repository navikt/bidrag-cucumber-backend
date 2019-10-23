package no.nav.bidrag.cucumber

import no.nav.bidrag.commons.CorrelationId
import org.springframework.http.*
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpStatusCodeException

class RestTjeneste(
        private val alias: String,
        private val rest: RestTemplateMedBaseUrl
) {
    internal var endpointUrl: String = alias
    internal var httpStatus: HttpStatus = HttpStatus.I_AM_A_TEAPOT
    internal var response: String? = null

    constructor(alias: String) : this(alias, Fasit().hentRestTemplateFor(alias))

    fun exchangeGet(endpointUrl: String): ResponseEntity<String> {
        this.endpointUrl = rest.baseUrl + endpointUrl
        val header = correlationIdHeader()

        val stringEntity: ResponseEntity<String> = try {
            rest.template.exchange(endpointUrl, HttpMethod.GET, HttpEntity(null, header), String::class.java)
        } catch (e: HttpStatusCodeException) {
            ResponseEntity(addAliasToHeader(), e.statusCode)
        }

        response = stringEntity.body
        httpStatus = stringEntity.statusCode

        return stringEntity
    }

    private fun correlationIdHeader(): HttpHeaders {
        val headers = HttpHeaders()
        headers.add(CorrelationId.CORRELATION_ID_HEADER, Environment.createCorrelationHeader())

        return headers
    }

    private fun addAliasToHeader(): MultiValueMap<String, String> {
        val httpHeaders = HttpHeaders()
        httpHeaders.add("ERROR_REST_SERVICE", alias)

        return httpHeaders
    }

    fun put(endpointUrl: String, journalpostJson: String) {
        this.endpointUrl = rest.baseUrl + endpointUrl
        val headers = correlationIdHeader()
        headers.contentType = MediaType.APPLICATION_JSON

        val jsonEntity = HttpEntity(journalpostJson, headers)

        try {
            println(jsonEntity)
            println(rest.template.exchange(endpointUrl, HttpMethod.PUT, jsonEntity, String::class.java))
        } catch (e: HttpStatusCodeException) {
            System.err.println("OPPDATERING FEILET: ${this.endpointUrl}: $e")
            throw e
        }
    }
}
