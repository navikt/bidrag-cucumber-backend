package no.nav.bidrag.cucumber

import no.nav.bidrag.commons.web.CorrelationIdFilter
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriTemplateHandler
import java.net.URI

private const val ENVIRONMENT = "ENVIRONMENT"
private const val FASIT_URL = "https://fasit.adeo.no/api/v2/resources"
private const val FASIT_ZONE = "fss"
private const val OIDC_ALIAS = "bidrag-dokument-ui-oidc"
private const val REDIRECT_ISSO_URI = "https://bidrag-dokument-ui.nais.preprod.local/isso"
private const val TEST_USER = "TEST_USER"
private const val TEST_USER_PASSWORD = "TEST_PASS"
private const val X_ENHETSNUMMER_HEADER = "X-Enhetsnummer"

class RestTjeneste(private val restTemplate: RestTemplate) {

    var response: String? = null

    constructor(alias: String) : this(Fasit().hentRestTemplateFor(alias))

    fun exchangeGet(relativSti: String): ResponseEntity<String> {
        val stringEntity: ResponseEntity<String> = try {
            restTemplate.getForEntity(relativSti, String::class.java)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

        response = stringEntity.body
        return stringEntity
    }
}

class Fasit(val fasitUrl: String = FASIT_URL) {
    fun hentRestTemplateFor(alias: String): RestTemplate {
        val baseUrl = hentFor(alias)
        val restTemplate = hentMedCorrelationIdHeader()
        restTemplate.uriTemplateHandler = BaseUrlTemplateHandler(baseUrl)
        return restTemplate
    }

    private fun hentFor(alias: String): String {
        return "https://todo/hent/for/$alias"
    }

    private fun hentMedCorrelationIdHeader(): HttpHeaderRestTemplate {
        val httpHeaderRestTemplate = HttpHeaderRestTemplate()
        httpHeaderRestTemplate.addHeaderGenerator(CorrelationIdFilter.CORRELATION_ID_HEADER, CorrelationIdFilter::fetchCorrelationIdForThread)

        return httpHeaderRestTemplate
    }

    private class BaseUrlTemplateHandler(val baseUrl: String) : UriTemplateHandler {
        override fun expand(uriTemplate: String, uriVariables: MutableMap<String, *>): URI {
            return URI.create(baseUrl) // todo
        }

        override fun expand(uriTemplate: String, vararg uriVariables: Any?): URI {
            return URI.create(baseUrl) // todo
        }
    }
}

class Environment {
    companion object ManagedEnvironment {
        internal var environment: String? = null
    }

    fun fetch(): String {
        if (environment != null) {
            return environment as String
        }

        environment = System.getenv(ENVIRONMENT)

        return environment ?: throw IllegalStateException("Ikke angitt miljø for variable ENVIRONMENT")
    }

    fun use(miljo: String) {
        environment = miljo
    }

    fun fetchToken(): Any? {
        return null
    }

    fun fetchUsrerToken(): Any? {
        return null
    }
}