package no.nav.bidrag.cucumber

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

internal class Simple {
    companion object {
        private val objectMapper = ObjectMapper()
        private val supportedApplications = HashSet<String>(listOf(
                VAULT_SUPPORT_BIDRAG_HENDELSE_PRODUCER,
                VAULT_SUPPORT_BIDRAG_HENDELSE
        ))
    }

    internal fun supports(applicationName: String) = supportedApplications.contains(applicationName)

    internal fun hentFullContextPath(applicationName: String): String {
        if (Environment.offline) {
            return "http://localhost:8080${CONTEXT_PATH_PER_APPLICATION[applicationName]}"
        }

        val miljo = Environment.fetch()
        val projFolder = System.getProperty(PROJECT_NAIS_FOLDER)

        logFilepaths(projFolder, applicationName, miljo)

        val jsonPath = "$projFolder/$applicationName/nais/$miljo.json"
        val jsonFileAsMap = objectMapper.readValue(File(jsonPath), Map::class.java)
        val ingressPreprod = jsonFileAsMap["ingress_preprod"]

        return "${ingressPreprod}${CONTEXT_PATH_PER_APPLICATION[applicationName]}"
                .replace("//", "/").replace("https:/", "https://")
    }

    private fun logFilepaths(projPath: String, applicationName: String, miljo: String) {
        val projFolder = File(projPath)
        val applfolder = File("$projFolder/$applicationName")
        val naisFolder = File("$projFolder/$applicationName/nais")
        val jsonFile = File("$projFolder/$applicationName/nais/$miljo.json")

        println("> projFolder exists: ${projFolder.exists()}, path: $projFolder")
        println("> applFolder exists: ${applfolder.exists()}, path: $applfolder")
        println("> naisFolder exists: ${naisFolder.exists()}, path: $naisFolder")
        println("> jsonFile   exists: ${jsonFile.exists()}, path: $jsonFile")
    }
}
