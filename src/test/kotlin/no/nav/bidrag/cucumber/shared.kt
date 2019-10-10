package no.nav.bidrag.cucumber

import no.nav.bidrag.commons.web.CorrelationIdFilter
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.UriTemplateHandler
import java.net.URI

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

class Fasit {
    private var fasitTemplate = RestTemplate()
    private var offline = false

    fun hentRestTemplateFor(alias: String): RestTemplate {
        val baseUrl = hentFor(alias) ?: throw IllegalStateException("Unable to find '$alias' from $FASIT_URL (${offlineStatus("rest")}))")

        val restTemplate = hentMedCorrelationIdHeader()
        restTemplate.uriTemplateHandler = BaseUrlTemplateHandler(baseUrl)

        return restTemplate
    }

    private fun offlineStatus(type: String) = if (offline) "check fasit.offline.$type.json" else "connectesd to url"

    private fun hentFor(alias: String): String? {
        val miljo = Environment().fetch()
        val builder = UriComponentsBuilder
                .fromHttpUrl(FASIT_URL).path("/")
                .query("type=restservice")
                .query("alias=$alias")
                .query("environment=$miljo")

        val fasitJson: String? = try {
            fasitTemplate.getForObject<String>(builder.toUriString(), String::class.java, "type=restservice")
        } catch (e: Exception) {
            offline = true
            Fasit::class.java.getResource("fasit.offline.rest.json").readText(Charsets.UTF_8)
        }

        println(fasitJson)

        return fasitJson
    }

    private fun hentMedCorrelationIdHeader(): HttpHeaderRestTemplate {
        val httpHeaderRestTemplate = HttpHeaderRestTemplate()
        httpHeaderRestTemplate.addHeaderGenerator(CorrelationIdFilter.CORRELATION_ID_HEADER, CorrelationIdFilter::fetchCorrelationIdForThread)
        httpHeaderRestTemplate.addHeaderGenerator(HttpHeaders.AUTHORIZATION, this::fetchIdToken)

        return httpHeaderRestTemplate
    }

    private fun fetchIdToken(): String {
        return "Bearer todo: id token"
    }

    private class BaseUrlTemplateHandler(val baseUrl: String) : UriTemplateHandler {
        override fun expand(uriTemplate: String, uriVariables: MutableMap<String, *>): URI {
            return URI.create(baseUrl)
        }

        override fun expand(uriTemplate: String, vararg uriVariables: Any?): URI {
            return URI.create(baseUrl)
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

interface FasitService {
    fun fetchResource(type: String, environment: String, alias: String)
}

data class FasitResource(
        val type: String? = null,
        val environment: String? = null,
        val url: String? = null
)
