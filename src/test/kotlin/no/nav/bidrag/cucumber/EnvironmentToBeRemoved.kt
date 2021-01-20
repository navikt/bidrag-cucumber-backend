package no.nav.bidrag.cucumber

internal open class EnvironmentToBeRemoved {

    companion object {
        private const val ENV_FEATURE = "feature"
        private const val ENV_MAIN = "main"

        internal val miljo by lazy {
            System.getProperty(ENVIRONMENT) ?: throw IllegalStateException("Fant ikke miljø for kjøring")
        }

        internal val naisProjectFolder: String by lazy {
            System.getProperty(PROJECT_NAIS_FOLDER) ?: throw IllegalStateException("Det er ikke oppgitt ei mappe for nais prosjekt")
        }

        fun testUser() = System.getProperty(CREDENTIALS_TEST_USER) ?: throw IllegalStateException("Fant ikke testbruker (ala z123456)")

        fun user() = System.getProperty(CREDENTIALS_USERNAME) ?: throw IllegalStateException("Fant ikke nav-bruker (ala [x]123456)")
    }
}