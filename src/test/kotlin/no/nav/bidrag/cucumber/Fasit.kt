package no.nav.bidrag.cucumber

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.commons.CorrelationId
import no.nav.bidrag.commons.web.CorrelationIdFilter
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

open class Fasit {

    internal var fasitTemplate = RestTemplate()

    internal fun hentRestTemplateFor(alias: String): RestTemplateMedBaseUrl {
        val miljo = Environment().fetch()
        val resourceUrl = hentRessursUrl(URL_FASIT, "type=restservice", "alias=$alias", "environment=$miljo")
        val fasitRessurs = hentFasitReurs(resourceUrl, alias, "rest")
        val httpHeaderRestTemplate = Environment().hentRestTemplate(HttpHeaderRestTemplate(), fasitRessurs.url())
        httpHeaderRestTemplate.addHeaderGenerator(CorrelationIdFilter.CORRELATION_ID_HEADER, CorrelationId::fetchCorrelationIdForThread)
        httpHeaderRestTemplate.addHeaderGenerator(HttpHeaders.AUTHORIZATION, Sikkerhet()::fetchIdToken)

        return RestTemplateMedBaseUrl(httpHeaderRestTemplate, fasitRessurs.url())
    }

    internal fun hentRessursUrl(url: String, vararg queries: String): String {
        val resourceUrl = UriComponentsBuilder.fromHttpUrl(url)

        queries.forEach { resourceUrl.query(it) }

        return resourceUrl.toUriString()
    }

    internal fun hentFasitReurs(resourceUrl: String, alias: String, type: String): FasitResurs {
        val fasitJson = try {
            fasitTemplate.getForObject<String>(resourceUrl, String::class.java)
        } catch (e: Exception) {
            Environment.offline = true
            Fasit::class.java.getResource("fasit.offline.$type.json").readText(Charsets.UTF_8)
        }

        val listeFraFasit = ObjectMapper().readValue(fasitJson, List::class.java)
        @Suppress("UNCHECKED_CAST") val listeOverRessurser: List<FasitResurs> = listeFraFasit.map { FasitResurs(it as Map<String, *>) }

        val fasitRessurs = listeOverRessurser.find { it.alias == alias }

        return fasitRessurs ?: throw IllegalStateException("Unable to find '$alias' from $URL_FASIT (${offlineStatus(type)}))")
    }

    private fun offlineStatus(type: String) = if (Environment.offline) "check fasit.offline.$type.json" else "connected to fasit.adeo.no"
}

class RestTemplateMedBaseUrl(val template: RestTemplate, val baseUrl: String)

data class FasitResurs(
        var alias: String = "not named",
        var environment: String = "no environment",
        var type: String = "no type",
        private val ressurser: MutableMap<String, String?> = HashMap()
) {
    constructor(jsonMap: Map<String, *>?) : this() {
        requireNotNull(jsonMap) { "cannot construct a fasit resource without a jsonMap" }

        alias = jsonMap.getOrDefault("alias", "not named") as String
        environment = jsonMap.getOrDefault("environment", "no environment") as String
        type = jsonMap.getOrDefault("type", "no type") as String

        @Suppress("UNCHECKED_CAST") val properties = jsonMap["properties"] as Map<String, String>
        ressurser["url"] = properties["url"]
        ressurser["issuerUrl"] = properties["issuerUrl"]
        ressurser["agentName"] = properties["agentName"]
        ressurser["passord.url"] = hentPassordUrl(jsonMap["secrets"])
    }

    private fun hentPassordUrl(secrets: Any?): String? {
        if (secrets != null) {
            @Suppress("UNCHECKED_CAST") val password = (secrets as Map<String, Map<String, String>>)["password"]
            return if (password != null) password["ref"] else null
        }

        return null
    }

    fun url() = ressurser["url"] ?: "ingen url for $alias"
    fun passordUrl() = ressurser["passord.url"] ?: "ingen url for $alias"
}
