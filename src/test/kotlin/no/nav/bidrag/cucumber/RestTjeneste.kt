package no.nav.bidrag.cucumber

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

        val stringEntity: ResponseEntity<String> = try {
            rest.template.getForEntity(endpointUrl, String::class.java)
        } catch (e: HttpStatusCodeException) {
            ResponseEntity(addAliasToHeader(), e.statusCode)
        }

        response = stringEntity.body
        httpStatus = stringEntity.statusCode

        return stringEntity
    }

    private fun addAliasToHeader(): MultiValueMap<String, String> {
        val httpHeaders = HttpHeaders()
        httpHeaders.add("ERROR_REST_SERVICE", alias)

        return httpHeaders
    }
}
