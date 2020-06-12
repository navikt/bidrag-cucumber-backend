package no.nav.bidrag.cucumber

import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriTemplateHandler
import java.net.URI

internal class Environment {

    companion object {
        private const val Q0 = "q0"

        private var environment: String? = null

        internal val offline by lazy {
            Fasit.hentFasitRessursSomJson(
                    Fasit.buildUriString(URL_FASIT, "type=restservice", "alias=BidragDokument", "environment=q0")
            ).offline
        }

        fun createCorrelationIdValue(): String {
            return "cucumber-${java.lang.Long.toHexString(System.currentTimeMillis())}"
        }

        fun fetch(): String {
            if (environment != null) {
                return environment as String
            }

            if (offline) {
                return Q0
            }

            environment = System.getProperty(ENVIRONMENT)

            return environment ?: Q0
        }

        fun testUser() = System.getProperty(CREDENTIALS_TEST_USER) ?: throw IllegalStateException("Fant ikke testbruker (ala z123456)")
        fun testAuthentication() = System.getProperty(CREDENTIALS_TEST_USER_AUTH)
                ?: throw IllegalStateException("Fant ikke passord til ${testUser()}")

        fun use(miljo: String) {
            environment = miljo
        }

        fun user() = System.getProperty(CREDENTIALS_USERNAME) ?: throw IllegalStateException("Fant ikke nav-bruker (ala [x]123456)")
        fun userAuthentication() = System.getProperty(CREDENTIALS_USER_AUTH) ?: throw IllegalStateException("Fant ikke passord til ${user()}")
    }

    internal fun initRestTemplate(url: String): RestTemplate {
        return setBaseUrlPa(RestTemplate(), url)
    }

    internal fun <T : RestTemplate> setBaseUrlPa(restTemplate: T, url: String): T {
        restTemplate.uriTemplateHandler = BaseUrlTemplateHandler(url)

        return restTemplate
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
}
