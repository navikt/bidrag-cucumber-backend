package no.nav.bidrag.cucumber

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
        val fasitResourceUrl = fasit.hentRessursUrl(
                URL_FASIT, "type=OpenIdConnect", "environment=$miljo", "alias=$OIDC_ALIAS", "zone=$FASIT_ZONE", "usage=false"
        )

        return "Bearer todo: id token"
    }
}
