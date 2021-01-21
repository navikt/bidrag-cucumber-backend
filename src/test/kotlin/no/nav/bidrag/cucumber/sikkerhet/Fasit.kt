package no.nav.bidrag.cucumber.sikkerhet

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.cucumber.URL_FASIT
import org.junit.platform.commons.logging.LoggerFactory
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

object Fasit {

    private val LOGGER = LoggerFactory.getLogger(Fasit::class.java)
    private val FASIT_REST_TEMPLATE = RestTemplate()

    internal fun buildUriString(url: String, vararg queries: String): String {
        val resourceUrl = UriComponentsBuilder.fromHttpUrl(url)
        queries.forEach { resourceUrl.query(it) }

        return resourceUrl.toUriString()
    }

    internal fun hentRessurs(vararg queries: String): FasitRessurs {
        val resourceUrl = buildUriString(URL_FASIT, *queries)
        return hentFasitRessurs(resourceUrl, queries.first().substringAfter("="))
    }

    private fun hentFasitRessursSomJson(resourceUrl: String): FasitJson {
        val fasitJson: String? = try {
            FASIT_REST_TEMPLATE.getForObject(resourceUrl, String::class.java)
        } catch (e: ResourceAccessException) {
            LOGGER.error(e) { "Unable to getForObject($resourceUrl, ${String::class.java})" }
            null
        }

        return if (fasitJson != null) FasitJson(fasitJson) else throw IllegalStateException("Unable to find resource with $resourceUrl")
    }

    internal fun hentFasitRessurs(resourceUrl: String, alias: String): FasitRessurs {
        val fasitJson = hentFasitRessursSomJson(resourceUrl)
        val listeFraFasit: List<Map<String, *>> = mapFasitJsonTilListeAvRessurser(fasitJson)
        val listeOverRessurser: List<FasitRessurs> = listeFraFasit.map { FasitRessurs(it) }
        val fasitRessurs = listeOverRessurser.find { it.alias == alias }

        return fasitRessurs ?: throw IllegalStateException("Unable to find '$alias' from $URL_FASIT")
    }

    @Suppress("UNCHECKED_CAST")
    private fun mapFasitJsonTilListeAvRessurser(fasitJson: FasitJson) = ObjectMapper().readValue(fasitJson.json, List::class.java) as List<Map<String, Any>>

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

    internal data class FasitJson(var json: String)
}
