package no.nav.bidrag.cucumber

import org.springframework.http.HttpMethod

class Sikkerhet {
    private val fasit = Fasit()

    internal fun fetchIdToken(): String {
        if (Environment.offline) {
            val restTemplate = Environment().initRestTemplate("http://localhost:8090/bidrag-dokument")

            return try {
                "Bearer " + restTemplate.getForObject<String>("/local/jwt", String::class.java)
            } catch (e: Exception) {
                val exception = "${e.javaClass.simpleName}: ${e.message}"
                println(exception)
                exception
            }
        }

        val miljo = Environment().fetch()
        val openIdConnect = "OpenIdConnect"
        val fasitRessursUrl = fasit.hentRessursUrl(
                URL_FASIT, "type=$openIdConnect", "environment=$miljo", "alias=$OIDC_ALIAS", "zone=$FASIT_ZONE", "usage=false"
        )

        val fasitRessurs = fasit.hentFasitReurs(fasitRessursUrl, OIDC_ALIAS, openIdConnect)
        val passordOpenAm = Environment().initRestTemplate(fasitRessurs.passordUrl()).exchange("/", HttpMethod.GET, null, String::class.java).body

        return "Bearer todo: id token"
    }
}
