package no.nav.bidrag.cucumber

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.security.oidc.test.support.jersey.TestTokenGeneratorResource
import org.apache.tomcat.util.codec.binary.Base64
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

class Sikkerhet {
    private val fasit = Fasit()

    internal fun fetchIdToken(): String {
        if (Environment.offline) {
            val testTokenGeneratorResource = TestTokenGeneratorResource()
            return "Bearer " + testTokenGeneratorResource.issueToken("localhost-idtoken")
        }

        try {
            return fetchOnlineIdToken()
        } catch (e: RuntimeException) {
            val exception = "${e.javaClass.name}: ${e.message} - ${e.stackTrace.filter { it.fileName != null && it.fileName!!.endsWith("kt") }.first()}"
            System.err.println("Feil ved henting av online id token, ${exception}")
            throw e
        }
    }

    private fun fetchOnlineIdToken(): String {
        val miljo = Environment.fetch()
        val openIdConnectFasitRessurs = hentOpenIdConnectFasitRessurs(miljo)
        val passordOpenAm = hentOpenAmPassord(openIdConnectFasitRessurs)
        val tokenIdForAuthenticatedTestUser = hentTokenIdForTestbruker()
        val codeFraLocationHeader = hentCodeFraLocationHeader(tokenIdForAuthenticatedTestUser)

        return "Bearer " + hentIdToken(codeFraLocationHeader, passordOpenAm)
    }

    private fun hentOpenIdConnectFasitRessurs(miljo: String): FasitRessurs {
        val openIdConnect = "OpenIdConnect"
        val fasitRessursUrl = Fasit.buildUriString(
                URL_FASIT, "type=$openIdConnect", "environment=$miljo", "alias=$ALIAS_OIDC", "zone=$FASIT_ZONE", "usage=false"
        )

        return fasit.hentFasitRessurs(fasitRessursUrl, ALIAS_OIDC, openIdConnect)
    }

    private fun hentOpenAmPassord(openIdConnectFasitRessurs: FasitRessurs): String {
        val auth = "${Environment.user()}:${Environment.userAuthentication()}"
        val httpEntityWithAuthorizationHeader = initHttpEntity(
                header(HttpHeaders.AUTHORIZATION, "Basic " + String(Base64.encodeBase64(auth.toByteArray(Charsets.UTF_8))))
        )

        return Environment().initRestTemplate(openIdConnectFasitRessurs.passordUrl())
                .exchange("/", HttpMethod.GET, httpEntityWithAuthorizationHeader, String::class.java)
                .body ?: throw IllegalStateException("fant ikke passord for bruker på open am")
    }

    private fun hentTokenIdForTestbruker(): String {
        val httpEntityWithHeaders = initHttpEntity(
                header(HttpHeaders.CACHE_CONTROL, "no-cache"),
                header(HttpHeaders.CONTENT_TYPE, "application/json"),
                header(X_OPENAM_USER_HEADER, Environment.testUser()),
                header(X_OPENAM_PASSW_HEADER, Environment.testAuthentication())
        )

        val authJson = RestTemplate().exchange(URL_ISSO, HttpMethod.POST, httpEntityWithHeaders, String::class.java)
                .body ?: throw IllegalStateException("fant ikke json for testbruker")

        val authMap = ObjectMapper().readValue(authJson, Map::class.java)

        return authMap.get("tokenId") as String? ?: throw IllegalStateException("Fant ikke id token i json for testbruker")
    }

    private fun hentCodeFraLocationHeader(tokenIdForAuthenticatedTestUser: String): String {
        val httpEntityWithHeaders = initHttpEntity(
                "client_id=bidrag-ui-${Environment.fetch()}&response_type=code&redirect_uri=$URL_ISSO_REDIRECT&decision=allow&csrf=$tokenIdForAuthenticatedTestUser&scope=openid",
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
        val openApAuth = "$ALIAS_BIDRAG_UI-${Environment.fetch()}:$passordOpenAm"
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

    private fun initHttpEntity(vararg headers: Map.Entry<String, String>): HttpEntity<*>? {
        return initHttpEntity(null, *headers)
    }

    private fun initHttpEntity(data: String?, vararg headers: Map.Entry<String, String>): HttpEntity<*>? {
        val linkedMultiValueMap = LinkedMultiValueMap<String, String>()
        headers.forEach { linkedMultiValueMap.add(it.key, it.value) }
        val httpHeaders = HttpHeaders(linkedMultiValueMap)

        return HttpEntity(data, httpHeaders)
    }

    private fun header(headerName: String, headerValue: String): Map.Entry<String, String> {
        return java.util.Map.of(headerName, headerValue).entries.first()
    }
}
