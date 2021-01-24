package no.nav.bidrag.cucumber

import no.nav.bidrag.cucumber.input.IntegrationInput
import org.slf4j.LoggerFactory
import java.io.File

internal object Environment {
    private var integrationInput: IntegrationInput? = null
    private val LOGGER = LoggerFactory.getLogger(Environment::class.java)
    val naisApplications: Set<String> by lazy {
        findNaisApplications(fetchIntegrationInput())
    }

    val namespace: String by lazy { // brukes for å hente fasit-ressurser
        if (fetchIntegrationInput().environment == "main") "q0" else "q1"
    }

    fun fetchTestAuthentication() = System.getProperty(CREDENTIALS_TEST_USER_AUTH) ?: throw IllegalStateException("Fant ikke passord til test bruker")
    fun fetchIntegrationInput() = integrationInput ?: readNonCahcedIntegrationInput()

    private fun readNonCahcedIntegrationInput(): IntegrationInput {
        if (integrationInput == null) {
            val jsonPath = System.getProperty(INTEGRATION_INPUT) ?: System.getenv(INTEGRATION_INPUT) ?: return initFromEnvironmentAboutToBeRemoved()

            LOGGER.info("Leser IngegrationInput fra $jsonPath")

            integrationInput = IntegrationInput.read(jsonPath)
        }

        return this.integrationInput!!
    }

    private fun initFromEnvironmentAboutToBeRemoved(): IntegrationInput {
        LOGGER.info("Leser IngegrationInput fra System.getProperty(...)")
        LOGGER.info("Fant ikke $INTEGRATION_INPUT i ${System.getProperties().keys}")

        return IntegrationInput(
            environment = EnvironmentToBeRemoved.miljo,
            naisProjectFolder = EnvironmentToBeRemoved.naisProjectFolder,
            userTest = EnvironmentToBeRemoved.testUser,
            userTestAuth = fetchTestAuthentication()
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
