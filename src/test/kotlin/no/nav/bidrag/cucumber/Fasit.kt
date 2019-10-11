package no.nav.bidrag.cucumber

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.commons.web.CorrelationIdFilter
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.UriTemplateHandler
import java.net.URI

internal class Fasit {
    private var fasitTemplate = RestTemplate()
    private var offline = false

    internal fun hentRestTemplateFor(alias: String): RestTemplateMedBaseUrl {
        val fasitResource = hentRessursForRestTemplate(alias)
                ?: throw IllegalStateException("Unable to find '$alias' from $FASIT_URL (${offlineStatus("rest")}))")

        val restTemplate = hentMedCorrelationIdHeader()
        restTemplate.uriTemplateHandler = BaseUrlTemplateHandler(fasitResource.url)

        return RestTemplateMedBaseUrl(restTemplate, fasitResource.url)
    }

    private fun offlineStatus(type: String) = if (offline) "check fasit.offline.$type.json" else "connectesd to url"

    private fun hentRessursForRestTemplate(alias: String): FasitResource? {
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

        val listeFraFasit = ObjectMapper().readValue(fasitJson, List::class.java)
        @Suppress("UNCHECKED_CAST") val listeOverRessurser: List<FasitResource> = listeFraFasit.map { FasitResource(it as Map<String, String>) }

        return listeOverRessurser.find { it.alias == alias }
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
            return URI.create(baseUrl + uriTemplate)
        }
    }
}

data class RestTemplateMedBaseUrl(val template: RestTemplate, val baseUrl: String)

internal class Environment {
    companion object {
        internal var environment: String? = null
    }

    fun fetch(): String {
        if (environment != null) {
            return environment as String
        }

        environment = System.getProperty(ENVIRONMENT)

        return environment ?: "q0"
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

internal data class FasitResource(
        var alias: String = "not named",
        var environment: String = "no environment",
        var type: String = "no type",
        var url: String = "somewhere"
) {
    constructor(jsonMap: Map<String, *>?) : this() {
        requireNotNull(jsonMap) { "cannot construct a fasit resource without a jsonMap" }

        alias = jsonMap.getOrDefault("alias", "not named") as String
        environment = jsonMap.getOrDefault("environment", "no environment") as String
        type = jsonMap.getOrDefault("type", "no type") as String

        @Suppress("UNCHECKED_CAST") val properties = jsonMap.get("properties") as Map<String, String>
        url = properties.getOrDefault("url", "no url for $alias")
    }
}
