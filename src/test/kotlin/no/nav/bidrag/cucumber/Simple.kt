package no.nav.bidrag.cucumber

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class Simple {
    companion object {
        private val objectMapper = ObjectMapper()
        private val supportedApplications = HashSet<String>(listOf(
                VAULT_SUPPORT_BIDRAG_HENDELSE_PRODUCER
        ))
    }

    fun supports(applicationName: String) = supportedApplications.contains(applicationName)

    internal fun hentFullContextPath(applicationName: String): String {
        if (Environment.offline) {
            return "http://localhost:8080" + CONTEXT_PATH_PER_APPLICATION[applicationName]
        }

        val miljo = Environment.fetch()
        val jsonPath = "${System.getProperty(PROJECT_NAIS_FOLDER)}/$applicationName/nais/$miljo.json"
        val jsonFileAsMap = objectMapper.readValue(File(jsonPath), Map::class.java)
        val ingressPreprod = jsonFileAsMap["ingress_preprod"]

        return ingressPreprod as String + CONTEXT_PATH_PER_APPLICATION[applicationName]
    }
}