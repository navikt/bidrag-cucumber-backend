package no.nav.bidrag.cucumber

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

internal class Fasit {

    companion object {
        private var fasitTemplate = RestTemplate()

        internal fun buildUriString(url: String, vararg queries: String): String {
            val resourceUrl = UriComponentsBuilder.fromHttpUrl(url)
            queries.forEach { resourceUrl.query(it) }

            return resourceUrl.toUriString()
        }

        internal fun hentFasitRessursSomJson(resourceUrl: String): FasitJson {
            val fasitJson = try {
                fasitTemplate.getForObject(resourceUrl, String::class.java)
            } catch (e: ResourceAccessException) {
                return FasitJson(offline = true)
            }

            return FasitJson(fasitJson, false)
        }

        internal fun hentFasitRessurs(vararg queries: String): FasitRessurs {
            val resourceUrl = buildUriString(URL_FASIT, *queries)
            return Fasit().hentFasitRessurs(resourceUrl, queries.first().substringAfter("="), queries[1].substringAfter("="))
        }
    }

    internal fun hentFullContextPath(alias: String): String {
        val namespace = Environment.fetchNamespace()
        val resourceUrl = buildUriString(URL_FASIT, "type=restservice", "alias=$alias", "environment=$namespace")
        val fasitRessurs = hentFasitRessurs(resourceUrl, alias, "rest")

        return fasitRessurs.url()
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

    internal data class FasitJson(var json: String? = null, val offline: Boolean)
}
