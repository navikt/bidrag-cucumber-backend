package no.nav.bidrag.cucumber

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.springframework.http.HttpHeaders
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.security.cert.X509Certificate

open class Fasit {

    companion object {
        private val cacheRestTemplateMedBaseUrl = CacheRestTemplateMedBaseUrl()
        private var fasitTemplate = RestTemplate()

        internal fun buildUriString(url: String, vararg queries: String): String {
            val resourceUrl = UriComponentsBuilder.fromHttpUrl(url)
            queries.forEach { resourceUrl.query(it) }

            return resourceUrl.toUriString()
        }

        internal fun hentFasitRessursSomJson(resourceUrl: String): FasitJson {
            val fasitJson = try {
                fasitTemplate.getForObject<String>(resourceUrl, String::class.java)
            } catch (e: ResourceAccessException) {
                return FasitJson(true)
            }

            return FasitJson(fasitJson, false)
        }

        internal fun hentFasitRessurs(vararg queries: String): FasitRessurs {
            val resourceUrl = buildUriString(URL_FASIT, *queries)
            return Fasit().hentFasitRessurs(resourceUrl, queries.first().substringAfter("="), queries[1].substringAfter("="))
        }
    }

    internal fun hentRestTemplateFor(alias: String): RestTemplateMedBaseUrl {
        return cacheRestTemplateMedBaseUrl.hentEllerLag(alias)
    }

    private fun initRestTemplate(alias: String): RestTemplateMedBaseUrl {
        val miljo = Environment.fetch()
        val resourceUrl = buildUriString(URL_FASIT, "type=restservice", "alias=$alias", "environment=$miljo")
        val fasitRessurs = hentFasitRessurs(resourceUrl, alias, "rest")
        val httpComponentsClientHttpRequestFactory = hentHttpRequestFactorySomIgnorererHttps()
        val httpHeaderRestTemplate = Environment()
                .setBaseUrlPa(HttpHeaderRestTemplate(httpComponentsClientHttpRequestFactory), fasitRessurs.url())

        httpHeaderRestTemplate.addHeaderGenerator(HttpHeaders.AUTHORIZATION) { Sikkerhet().fetchIdToken() }

        return RestTemplateMedBaseUrl(httpHeaderRestTemplate, fasitRessurs.url())
    }

    private fun hentHttpRequestFactorySomIgnorererHttps(): HttpComponentsClientHttpRequestFactory {
        val acceptingTrustStrategy = { _: Array<X509Certificate>, _: String -> true }
        val sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build()

        val csf = SSLConnectionSocketFactory(sslContext)

        val httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build()

        val requestFactory = HttpComponentsClientHttpRequestFactory()

        requestFactory.httpClient = httpClient

        return requestFactory
    }

    internal fun hentFasitRessurs(resourceUrl: String, alias: String, type: String): FasitRessurs {
        val fasitJson = hentFasitRessursSomJson(resourceUrl)
        val listeFraFasit: List<Map<String, *>> = mapFasitJsonTilListeAvRessurser(fasitJson, type)
        val listeOverRessurser: List<FasitRessurs> = listeFraFasit.map { FasitRessurs(it) }
        val fasitRessurs = listeOverRessurser.find { it.alias == alias }

        return fasitRessurs ?: throw IllegalStateException("Unable to find '$alias' from $URL_FASIT (${offlineStatus(type)}))")
    }

    @Suppress("UNCHECKED_CAST")
    private fun mapFasitJsonTilListeAvRessurser(fasitJson: FasitJson, type: String): List<Map<String, Any>> {
        return if (fasitJson.offline)
            ObjectMapper().readValue(Fasit::class.java.getResource("fasit.offline.$type.json").readText(Charsets.UTF_8), List::class.java)
                    as List<Map<String, Any>>
        else
            ObjectMapper().readValue(fasitJson.json, List::class.java) as List<Map<String, Any>>
    }

    private fun offlineStatus(type: String) = if (Environment.offline) "check fasit.offline.$type.json" else "connected to fasit.adeo.no"

    private class CacheRestTemplateMedBaseUrl(private val cache: MutableMap<String, RestTemplateMedBaseUrl> = HashMap()) {

        fun hentEllerLag(alias: String): RestTemplateMedBaseUrl {
            if (cache.containsKey(alias)) {
                return cache[alias]!!
            }

            val restTemplateMedBaseUrl = Fasit().initRestTemplate(alias)

            cache[alias] = restTemplateMedBaseUrl

            return restTemplateMedBaseUrl
        }
    }
}

class RestTemplateMedBaseUrl(val template: RestTemplate, val baseUrl: String)

data class FasitRessurs(
        internal val alias: String,
        private val type: String,
        private val ressurser: MutableMap<String, String?> = HashMap()
) {
    constructor(jsonMap: Map<String, *>) : this(
            alias = jsonMap["alias"] as String,
            type = jsonMap["type"] as String
    ) {
        @Suppress("UNCHECKED_CAST") val properties = jsonMap["properties"] as Map<String, String>
        ressurser["url"] = properties["url"]
        ressurser["issuerUrl"] = properties["issuerUrl"]
        ressurser["agentName"] = properties["agentName"]
        ressurser["passord.url"] = hentPassordUrl(jsonMap["secrets"])
    }

    private fun hentPassordUrl(secrets: Any?): String? {
        if (secrets != null) {
            @Suppress("UNCHECKED_CAST") val password = (secrets as Map<String, Map<String, String?>>)["password"]
            return if (password != null) password["ref"] else null
        }

        return null
    }

    fun url() = ressurser["url"] ?: "ingen url for $alias"
    fun passordUrl() = ressurser["passord.url"] ?: "ingen url for $alias"
}

internal class FasitJson(val json: String?, val offline: Boolean) {
    constructor(offline: Boolean) : this(null, offline)
}
