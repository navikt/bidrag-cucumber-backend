package no.nav.bidrag.cucumber

import org.slf4j.LoggerFactory
import java.io.File

internal object Environment {

    private const val ENV_FEATURE = "feature"
    private const val ENV_MAIN = "main"
    private val LOGGER = LoggerFactory.getLogger(Environment::class.java)
    private var namespaceForEnvironment: Map<String, String> = mapOf(Pair(ENV_MAIN, "q0"), Pair(ENV_FEATURE, "q1"))

    internal val namespace: String by lazy {
        fetchNamespace()
    }

    internal val offline by lazy {
        Fasit.hentFasitRessursSomJson(
            Fasit.buildUriString(URL_FASIT, "type=restservice", "alias=BidragDokument", "environment=q0")
        ).offline
    }

    internal val miljo by lazy {
        System.getProperty(ENVIRONMENT) ?: throw IllegalStateException("Fant ikke miljø for kjøring")
    }

    internal val naisProjectFolder: String by lazy {
        System.getProperty(PROJECT_NAIS_FOLDER) ?: throw IllegalStateException("Det er ikke oppgitt ei mappe for nais prosjekt")
    }

    private val naisApplications: Set<String> by lazy {
        findNaisApplications()
    }

    internal fun createCorrelationIdValue(): String {
        return "cucumber-${java.lang.Long.toHexString(System.currentTimeMillis())}"
    }

    private fun fetchNamespace(): String {
        val namespaceForMain = namespaceForEnvironment.getValue(ENV_MAIN)

        if (offline) {
            LOGGER.info("We are offline, using namespace for main: $namespaceForMain")
            return namespaceForMain
        }

        val wantedNamespace = namespaceForEnvironment[miljo]

        if (wantedNamespace != null) {
            LOGGER.info("Using namespace '$wantedNamespace' for environment '$miljo'")
        } else {
            LOGGER.warn("Unable to find namespace for environment ($miljo), using namespace for main $namespaceForMain")
        }

        return wantedNamespace ?: namespaceForMain
    }

    fun isApplicationPresentInNaisProjectFolder(applicationNameOrAlias: String): Boolean {
        return naisApplications.contains(applicationNameOrAlias)
    }

    private fun findNaisApplications(): Set<String> {
        val discoveredNaisApplications = HashSet<String>()

        File(naisProjectFolder).walk().forEach {
            if (it.isDirectory) {
                discoveredNaisApplications.add(it.name)
            }
        }

        return discoveredNaisApplications
    }

    fun testUser() = System.getProperty(CREDENTIALS_TEST_USER) ?: throw IllegalStateException("Fant ikke testbruker (ala z123456)")
    fun testAuthentication() = System.getProperty(CREDENTIALS_TEST_USER_AUTH)
        ?: throw IllegalStateException("Fant ikke passord til ${testUser()}")

    fun user() = System.getProperty(CREDENTIALS_USERNAME) ?: throw IllegalStateException("Fant ikke nav-bruker (ala [x]123456)")
    fun userAuthentication() = System.getProperty(CREDENTIALS_USER_AUTH) ?: throw IllegalStateException("Fant ikke passord til ${user()}")
}
