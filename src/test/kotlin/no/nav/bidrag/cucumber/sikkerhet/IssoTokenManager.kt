package no.nav.bidrag.cucumber.sikkerhet

import com.fasterxml.jackson.databind.ObjectMapper
import io.cucumber.gherkin.Parser
import no.nav.bidrag.cucumber.ALIAS_BIDRAG_UI
import no.nav.bidrag.cucumber.ALIAS_OIDC
import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.EnvironmentToBeRemoved
import no.nav.bidrag.cucumber.FASIT_ZONE
import no.nav.bidrag.cucumber.RestTjeneste
import no.nav.bidrag.cucumber.URL_FASIT
import no.nav.bidrag.cucumber.URL_ISSO
import no.nav.bidrag.cucumber.URL_ISSO_ACCESS_TOKEN
import no.nav.bidrag.cucumber.URL_ISSO_AUTHORIZE
import no.nav.bidrag.cucumber.URL_ISSO_REDIRECT
import no.nav.bidrag.cucumber.X_OPENAM_PASSW_HEADER
import no.nav.bidrag.cucumber.X_OPENAM_USER_HEADER
import org.apache.tomcat.util.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

internal object IssoTokenManager {

    private const val OPEN_AM_PASSWORD = "OPEN AM PASSWORD"
    private const val OPEN_ID_FASIT = "OPEN ID FASIT"
    private const val TEST_USER_AUTH_TOKEN = "TEST TOKEN AUTH TOKEN"
    private val LOGGER = LoggerFactory.getLogger(Parser.ITokenMatcher::class.java)
    private val SECURED_CACHE: MutableMap<String, Any> = HashMap()

    internal fun fetchOnlineIdToken(namespace: String): String {
        SECURED_CACHE[OPEN_ID_FASIT] = SECURED_CACHE[OPEN_ID_FASIT] ?: hentOpenIdConnectFasitRessurs(namespace)
        SECURED_CACHE[OPEN_AM_PASSWORD] = SECURED_CACHE[OPEN_AM_PASSWORD] ?: hentOpenAmPwd(SECURED_CACHE[OPEN_ID_FASIT] as Fasit.FasitRessurs)
        SECURED_CACHE[TEST_USER_AUTH_TOKEN] = SECURED_CACHE[TEST_USER_AUTH_TOKEN] ?: hentTokenIdForTestbruker()
        val codeFraLocationHeader = hentCodeFraLocationHeader(SECURED_CACHE[TEST_USER_AUTH_TOKEN] as String)

        LOGGER.info("Fetched id token for ${Environment.fetchIntegrationInput().userTest}")

        return "Bearer " + hentIdToken(codeFraLocationHeader, SECURED_CACHE[OPEN_AM_PASSWORD] as String)
    }

    private fun hentOpenIdConnectFasitRessurs(namespace: String): Fasit.FasitRessurs {
        val fasitRessursUrl = Fasit.buildUriString(
            URL_FASIT, "type=OpenIdConnect", "environment=$namespace", "alias=$ALIAS_OIDC", "zone=$FASIT_ZONE", "usage=false"
        )

        val openIdConnectFasitRessurs = Fasit.hentFasitRessurs(fasitRessursUrl, ALIAS_OIDC)

        LOGGER.info("Hentet openIdConnectFasitRessurs: $openIdConnectFasitRessurs")

        return openIdConnectFasitRessurs
    }

    private fun hentOpenAmPwd(openIdConnectFasitRessurs: Fasit.FasitRessurs): String {
        val user = EnvironmentToBeRemoved.user
        val auth = "$user:${EnvironmentToBeRemoved.userAuthentication}"
        val httpEntityWithAuthorizationHeader = initHttpEntity(
            header(HttpHeaders.AUTHORIZATION, "Basic " + String(Base64.encodeBase64(auth.toByteArray(Charsets.UTF_8))))
        )

        LOGGER.info("Finding OpenAM password for $user from ${openIdConnectFasitRessurs.passordUrl()}")

        return RestTjeneste.initRestTemplate(openIdConnectFasitRessurs.passordUrl())
            .exchange("/", HttpMethod.GET, httpEntityWithAuthorizationHeader, String::class.java)
            .body ?: throw IllegalStateException("fant ikke passord for bruker på open am")
    }

    private fun hentTokenIdForTestbruker(): String {
        val testUser = Environment.fetchIntegrationInput().userTest
        val httpEntityWithHeaders = initHttpEntity(
            header(HttpHeaders.CACHE_CONTROL, "no-cache"),
            header(HttpHeaders.CONTENT_TYPE, "application/json"),
            header(X_OPENAM_USER_HEADER, testUser),
            header(X_OPENAM_PASSW_HEADER, Environment.fetchTestUserAuthentication())
        )

        LOGGER.info("Hent token id for $testUser in ${Environment.namespace} from $URL_ISSO")

        val authJson = RestTemplate().exchange(URL_ISSO, HttpMethod.POST, httpEntityWithHeaders, String::class.java)
            .body ?: throw IllegalStateException("fant ikke json for $testUser in ${Environment.namespace}")

        val authMap = ObjectMapper().readValue(authJson, Map::class.java)

        LOGGER.info("Setting up security for $testUser running in ${Environment.namespace}")

        return authMap["tokenId"] as String? ?: throw IllegalStateException("Fant ikke id token i json for $testUser in ${Environment.namespace}")
    }

    private fun hentCodeFraLocationHeader(tokenIdForAuthenticatedTestUser: String): String {
        val httpEntityWithHeaders = initHttpEntity(
            "client_id=bidrag-ui-${Environment.namespace}&response_type=code&redirect_uri=$URL_ISSO_REDIRECT&decision=allow&csrf=$tokenIdForAuthenticatedTestUser&scope=openid",
            header(HttpHeaders.CACHE_CONTROL, "no-cache"),
            header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded"),
            header(HttpHeaders.COOKIE, "nav-isso=$tokenIdForAuthenticatedTestUser")
        )

        val uri = RestTemplate().postForLocation(URL_ISSO_AUTHORIZE, httpEntityWithHeaders) ?: throw IllegalStateException("fant ikke location uri")

        val queryString = uri.query
        val queries = queryString.split("&")
        val codeQuery = queries.find { it.startsWith("code=") } ?: throw IllegalStateException("Fant ikke code i Location")

        return codeQuery.substringAfter("code=")
    }

    private fun hentIdToken(codeFraLocationHeader: String, passordOpenAm: String): String {
        val openApAuth = "$ALIAS_BIDRAG_UI-${Environment.namespace}:$passordOpenAm"
        val httpEntityWithHeaders = initHttpEntity(
            "grant_type=authorization_code&code=$codeFraLocationHeader&redirect_uri=$URL_ISSO_REDIRECT",
            header(HttpHeaders.AUTHORIZATION, "Basic " + String(Base64.encodeBase64(openApAuth.toByteArray(Charsets.UTF_8)))),
            header(HttpHeaders.CACHE_CONTROL, "no-cache"),
            header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
        )

        val accessTokenJson = RestTemplate().exchange(URL_ISSO_ACCESS_TOKEN, HttpMethod.POST, httpEntityWithHeaders, String::class.java)
            .body ?: throw IllegalStateException("fant ikke json med id token")

        val accessTokenMap = ObjectMapper().readValue(accessTokenJson, Map::class.java)

        return accessTokenMap["id_token"] as String? ?: throw IllegalStateException("fant ikke id_token i json")
    }

    private fun initHttpEntity(vararg headers: Map.Entry<String, String>): HttpEntity<*> {
        return initHttpEntity(null, *headers)
    }

    private fun initHttpEntity(data: String?, vararg headers: Map.Entry<String, String>): HttpEntity<*> {
        val linkedMultiValueMap = LinkedMultiValueMap<String, String>()
        headers.forEach { linkedMultiValueMap.add(it.key, it.value) }
        val httpHeaders = HttpHeaders(linkedMultiValueMap)

        return HttpEntity(data, httpHeaders)
    }

    private fun header(headerName: String, headerValue: String): Map.Entry<String, String> {
        return java.util.Map.of(headerName, headerValue).entries.first()
    }
}
