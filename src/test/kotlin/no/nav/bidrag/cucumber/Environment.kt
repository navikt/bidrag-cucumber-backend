package no.nav.bidrag.cucumber

import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriTemplateHandler
import java.net.URI

private const val Q0 = "q0"

internal class Environment {

    companion object ManagedEnvironment {
        internal var offline = false
        internal var user = System.getProperty(USERNAME) ?: throw IllegalStateException("Fant ikke brukernavn til kjøringen")
        internal var user_authentication = System.getProperty(USER_AUTH) ?: throw IllegalStateException("Fant ikke passord til nav-bruker")
        internal var testUser = System.getProperty(TEST_USER) ?: throw IllegalStateException("Fant ikke testbruker (ala z123456)")
        internal var testAuthentication = System.getProperty(TEST_USER_PASSWORD) ?: throw IllegalStateException("Fant ikke passord til testbruker")
        private var environment: String? = null

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

        fun use(miljo: String) {
            environment = miljo
        }
    }

    internal fun initRestTemplate(url: String): RestTemplate {
        return hentRestTemplate(RestTemplate(), url)
    }

    internal fun <T : RestTemplate> hentRestTemplate(restTemplate: T, url: String): T {
        restTemplate.uriTemplateHandler = BaseUrlTemplateHandler(url)

        return restTemplate
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
