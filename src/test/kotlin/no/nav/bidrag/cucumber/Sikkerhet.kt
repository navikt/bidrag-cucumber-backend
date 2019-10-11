package no.nav.bidrag.cucumber

object Sikkerhet : Fasit() {

    internal fun fetchIdToken(): String {
        val miljo = Environment().fetch()
        val fasitResourceUrl = hentRessursUrl(
                URL_FASIT, "type=OpenIdConnect", "environment=$miljo", "alias=$OIDC_ALIAS", "zone=$FASIT_ZONE", "usage=false"
        )

        return "Bearer todo: id token"
    }
}
