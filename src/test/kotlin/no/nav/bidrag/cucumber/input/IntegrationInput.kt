@file:Suppress("unused")

package no.nav.bidrag.cucumber.input

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.cucumber.Environment
import org.slf4j.LoggerFactory
import java.io.File

class IntegrationInput(
    var azureInputs: List<AzureInput> = emptyList(),
    var environment: String = "<not set>",
    var naisProjectFolder: String = "<not set>",
    var userTest: String = "<not set>",
) {
    val userTestAuth: String
        get() = Environment.fetchTestUserAuthentication()

    companion object {

        private val LOGGER = LoggerFactory.getLogger(IntegrationInput::class.java)

        fun read(jsonPath: String) = readFile(jsonPath) ?: throw IllegalStateException("Fant ikke json i angitt json-path: $jsonPath")

        private fun readFile(filePath: String): IntegrationInput? {
            LOGGER.info("Will try to read environment file from $filePath")
            val json = File(filePath).inputStream().readBytes().toString(Charsets.UTF_8)
            return ObjectMapper().readValue(json, IntegrationInput::class.java)
        }
    }

    fun fetchAzureInput(applicationName: String): AzureInput {
        val environmentName = fetchApplicationNameForEnvironment(applicationName)
        LOGGER.info("Henter $environmentName fra azureInputs")
        return azureInputs.find { it.name == environmentName } ?: throw IllegalStateException("Fant ikke '$environmentName' blant $azureInputs")
    }

    internal fun fetchApplicationNameForEnvironment(applicationName: String): String {
        if (environment == "main") {
            return applicationName
        }

        if (applicationName.endsWith("-feature")) {
            return applicationName
        }

        return "$applicationName-feature"
    }

    fun fetchTenantUsername(): String {
        val testUserUpperCase = userTest.toUpperCase()
        return "F_$testUserUpperCase.E_$testUserUpperCase@trygdeetaten.no"
    }
}
