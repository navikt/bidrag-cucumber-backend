package no.nav.bidrag.cucumber

import no.nav.security.oidc.test.support.jersey.TestTokenGeneratorResource
import org.apache.tomcat.util.codec.binary.Base64
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

class Sikkerhet {
    private val fasit = Fasit()

    internal fun fetchIdToken(): String {
        if (Environment.offline) {
            val testTokenGeneratorResource = TestTokenGeneratorResource()
            return "Bearer " + testTokenGeneratorResource.issueToken("localhost-idtoken")
        }

        val miljo = Environment.fetch()
        val fasitRessurs = hentFasitRessurs(miljo)
        val passordOpenAm = hentOpenAmPassord(fasitRessurs)
        val tokenId = hentTokenForTestUser(passordOpenAm)

        return "Bearer todo: id token"
    }

    private fun hentFasitRessurs(miljo: String): FasitResurs {
        val openIdConnect = "OpenIdConnect"
        val fasitRessursUrl = fasit.hentRessursUrl(
                URL_FASIT, "type=$openIdConnect", "environment=$miljo", "alias=$OIDC_ALIAS", "zone=$FASIT_ZONE", "usage=false"
        )

        val fasitRessurs = fasit.hentFasitRessurs(fasitRessursUrl, OIDC_ALIAS, openIdConnect)
        return fasitRessurs
    }

    private fun hentOpenAmPassord(fasitRessurs: FasitResurs): String {
        return Environment().initRestTemplate(
                fasitRessurs.passordUrl()).exchange("/", HttpMethod.GET, initEntityWithBasicAutorization(), String::class.java
        ).body ?: throw IllegalStateException("fant ikke passord for bruker på open am")
    }

    private fun hentTokenForTestUser(passordOpenAm: String?): String {
        return ""
    }

    private fun initEntityWithBasicAutorization(): HttpEntity<*>? {
        val httpHeaders = HttpHeaders()
        val auth = "${Environment.user()}:${Environment.userAuthentication()}"
        val encoded = Base64.encodeBase64(auth.toByteArray(Charsets.UTF_8))

        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Basic " + String(encoded))

        return HttpEntity(null, httpHeaders)
    }
}
