package no.nav.bidrag.cucumber

internal open class EnvironmentToBeRemoved {

    companion object {
        internal val miljo by lazy { fetchSystemProperty(ENVIRONMENT, "Fant ikke miljø for kjøring") }
        internal val naisProjectFolder: String by lazy { fetchSystemProperty(PROJECT_NAIS_FOLDER, "Det er ikke oppgitt ei mappe for nais prosjekt") }
        internal val user: String by lazy { fetchSystemProperty(CREDENTIALS_USERNAME, "Fant ikke nav-bruker (ala [x]123456)") }
        internal val testUser: String by lazy { fetchSystemProperty(CREDENTIALS_TEST_USER, "Fant ikke testbruker (ala z123456)") }
        internal val userAuthentication: String by lazy { fetchSystemProperty(CREDENTIALS_USER_AUTH, "Fant ikke passord til $user") }

        private fun fetchSystemProperty(property: String, errorMessage: String): String {
            return System.getProperty(property) ?: throw IllegalStateException(errorMessage)
        }
    }
}
