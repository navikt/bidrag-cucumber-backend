package no.nav.bidrag.cucumber

import java.io.File

internal object Environment {
    private var integrationInput: IntegrationInput? = null
    private val naisApplications: Set<String> by lazy {
        findNaisApplications(fetchIntegrationInput())
    }

    val namespace: String by lazy { // brukes for å hente fasit-ressurser
        if (fetchIntegrationInput().environment == "main") "q0" else "q1"
    }

    fun fetchIntegrationInput() = integrationInput ?: readIntegrationInput()

    private fun readIntegrationInput(): IntegrationInput {
        return cahceEnvironment()
    }

    private fun cahceEnvironment(): IntegrationInput {
        if (integrationInput == null) {
            System.getProperty(INTEGRATION_INPUT) ?: System.getenv(INTEGRATION_INPUT) ?: return initFromEnvironmentAboutToBeRemoved()

            integrationInput = IntegrationInput.fromJson()
        }

        return this.integrationInput!!
    }

    fun testAuthentication() = System.getProperty(CREDENTIALS_TEST_USER_AUTH)
        ?: throw IllegalStateException("Fant ikke passord til ${fetchIntegrationInput().userTest}")

    fun userAuthentication() = System.getProperty(CREDENTIALS_USER_AUTH)
        ?: throw IllegalStateException("Fant ikke passord til ${fetchIntegrationInput().userNav}")


    private fun initFromEnvironmentAboutToBeRemoved(): IntegrationInput {
        return IntegrationInput(
            environment = EnvironmentToBeRemoved.miljo,
            naisProjectFolder = EnvironmentToBeRemoved.naisProjectFolder,
            userNav = EnvironmentToBeRemoved.user(),
            userNavAuth = userAuthentication(),
            userTest = EnvironmentToBeRemoved.testUser(),
            userTestAuth = testAuthentication()
        )
    }

    fun isApplicationPresentInNaisProjectFolder(applicationNameOrAlias: String): Boolean {
        return naisApplications.contains(applicationNameOrAlias)
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
