package no.nav.bidrag.cucumber

import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriTemplateHandler
import java.net.URI

private const val Q0 = "q0"

internal class Environment {

    companion object ManagedEnvironment {

        // blir satt av Fasit når den forsøker å finne fasit-ressurs for en resttjeneste og feiler med org.springframework.web.client.ResourceAccessException
        internal var offline = false

        private var environment: String? = null

        fun createCorrelationHeader(): String {
            return "cucumber(${java.lang.Long.toHexString(System.currentTimeMillis())})"
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
        fun testAuthentication() = System.getProperty(CREDENTIALS_TEST_USER_AUTH) ?: throw IllegalStateException("Fant ikke passord til testbruker")

        fun use(miljo: String) {
            environment = miljo
        }

        fun user() = System.getProperty(CREDENTIALS_USERNAME) ?: throw IllegalStateException("Fant ikke brukernavn til kjøringen")
        fun userAuthentication() = System.getProperty(CREDENTIALS_USER_AUTH) ?: throw IllegalStateException("Fant ikke passord til nav-bruker")
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
