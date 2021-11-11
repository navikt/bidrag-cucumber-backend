package no.nav.bidrag.cucumber

import no.nav.bidrag.cucumber.input.IntegrationInput
import org.slf4j.LoggerFactory
import java.io.File

internal object Environment {
    private val LOGGER = LoggerFactory.getLogger(Environment::class.java)

    private var integrationInput: IntegrationInput? = null
    val naisApplications: Set<String> by lazy { findNaisApplications(fetchIntegrationInput()) }
    val namespace: String by lazy { // brukes for å hente fasit-ressurser
        if (fetchIntegrationInput().environment == "main") "q2" else "q1"
    }
    val tokenNamespace: String by lazy {
        if (fetchIntegrationInput().environment == "main") "q2" else "feature-q1"
    }
    val issoRedirectUrl: String by lazy {
        if (fetchIntegrationInput().environment == "main") "https://bidrag-ui.dev.adeo.no/isso" else "https://bidrag-ui-feature.dev.adeo.no/isso"
    }

    fun fetchIntegrationInput() = integrationInput ?: readAndCahcedIntegrationInput()
    fun fetchTestUser() = integrationInput?.userTest
    fun hasTestUser() = fetchTestUser() != null && fetchTestUser() != ""
    internal fun fetchTestUserAuthentication() = System.getProperty(CREDENTIALS_TEST_USER_AUTH) ?: throw IllegalStateException(
        "Fant ikke passord til test bruker"
    )

    private fun readAndCahcedIntegrationInput(): IntegrationInput {
        val jsonPath = System.getProperty(INTEGRATION_INPUT) ?: System.getenv(INTEGRATION_INPUT) ?: return initFromEnvironmentAboutToBeRemoved()
        LOGGER.info("Leser IntegrationInput fra $jsonPath")
        integrationInput = IntegrationInput.read(jsonPath)

        return this.integrationInput!!
    }

    private fun initFromEnvironmentAboutToBeRemoved(): IntegrationInput {
        LOGGER.info("Leser IngegrationInput fra System.getProperty(...)")
        LOGGER.info("Fant ikke \$INTEGRATION_INPUT blant ${System.getProperties().keys}")

        return IntegrationInput(
            environment = EnvironmentToBeRemoved.miljo,
            naisProjectFolder = EnvironmentToBeRemoved.naisProjectFolder,
            userTest = EnvironmentToBeRemoved.testUser
        )
    }

    private fun findNaisApplications(integrationInput: IntegrationInput): Set<String> {
        val discoveredNaisApplications = HashSet<String>()

        File(integrationInput.naisProjectFolder).walk().forEach {
            if (it.isDirectory) {
                discoveredNaisApplications.add(it.name)
            }
        }

        return discoveredNaisApplications
    }
}
