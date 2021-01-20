package no.nav.bidrag.cucumber

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

private val LOGGER = LoggerFactory.getLogger(NaisConfiguration::class.java)
private val ALIAS_TO_NAIS_APPLICATION: Map<String, String> = mapOf(
    Pair("bidragDokument", "bidrag-dokument"),
    Pair("bidragDokumentArkiv", "bidrag-dokument-arkiv"),
    Pair("bidragDokumentJournalpost", "bidrag-dokument-journalpost"),
    Pair("bidragDokumentTestdata", "bidrag-dokument-testdata"),
    Pair("bidragOrganisasjon", "bidrag-organisasjon"),
    Pair("bidragPerson", "bidrag-person"),
    Pair("bidragSak", "bidrag-sak"),
    Pair("bidragSjablon", "bidrag-sjablon")
)

internal object NaisConfiguration {

    private val envFilePathPerAppName: MutableMap<String, String> = HashMap()

    fun readNaisConfiguration(applicationOrAlias: String): String {
        val applicationName = determineAppklicationName(applicationOrAlias)

        if (!envFilePathPerAppName.containsKey(applicationName)) {
            fetchNaisConfiguration(applicationName)
        }

        return applicationName
    }

    private fun fetchNaisConfiguration(applicationName: String) {
        val integrationInput = Environment.fetchIntegrationInput()
        val applfolder = File("${integrationInput.naisProjectFolder}/$applicationName")
        val naisFolder = File("${integrationInput.naisProjectFolder}/$applicationName/nais")
        val envFile = fetchEnvFileByEnvironment(applicationName, integrationInput)

        LOGGER.info("> applFolder exists: ${applfolder.exists()}, path: $applfolder")
        LOGGER.info("> naisFolder exists: ${naisFolder.exists()}, path: $naisFolder")
        LOGGER.info("> envFile    exists: ${envFile.exists()}, path: $envFile")

        val canReadNaisEnvironment = applfolder.exists() && naisFolder.exists() && envFile.exists()

        if (canReadNaisEnvironment) {
            envFilePathPerAppName[applicationName] = envFile.absolutePath
        }
    }

    private fun fetchEnvFileByEnvironment(applicationName: String, integrationInput: IntegrationInput): File {
        val miljoJson = File("${integrationInput.naisProjectFolder}/$applicationName/nais/${integrationInput.environment}.json")

        if (miljoJson.exists()) {
            return miljoJson
        }

        val miljoYaml = File("${integrationInput.naisProjectFolder}/$applicationName/nais/${integrationInput.environment}.yaml")

        if (miljoYaml.exists()) {
            return miljoYaml
        }

        throw IllegalStateException(" Unable to find ${integrationInput.naisProjectFolder}/$applicationName/nais/${integrationInput.environment}.? (json or yaml)")
    }

    internal fun hentApplicationHostUrl(applicationNameOrAlias: String): String {

        val applicationName = determineAppklicationName(applicationNameOrAlias)
        val nameSpaceEnvPath = envFilePathPerAppName[applicationName]
            ?: throw IllegalStateException("no path for $applicationName in $envFilePathPerAppName")

        val ingresses = if (nameSpaceEnvPath.endsWith(".yaml")) {
            fetchIngressesFromYaml(nameSpaceEnvPath)
        } else {
            fetchIngressesFromJson(nameSpaceEnvPath)
        }

        return fetchIngress(ingresses).replace("//", "/").replace("https:/", "https://")
    }

    private fun fetchIngressesFromYaml(nameSpaceEnvPath: String): List<String> {
        val yamlReader = File(nameSpaceEnvPath).bufferedReader()
        val yamlMap = Yaml().load<Map<String, List<String>>>(yamlReader)

        return yamlMap.getValue("ingresses")
    }

    private fun fetchIngressesFromJson(nameSpaceEnvPath: String): List<String> {
        val jsonFileAsMap = readWithGson(nameSpaceEnvPath)

        for (json in jsonFileAsMap) {
            println(json)
        }

        @Suppress("UNCHECKED_CAST") return jsonFileAsMap["ingresses"] as List<String>
    }

    private fun fetchIngress(ingresses: List<String?>): String {

        for (ingress in ingresses) {
            if (ingress?.contains(Regex("dev.adeo"))!!) {
                return ingress
            }
        }

        for (ingress in ingresses) {
            if (ingress?.contains(Regex("preprod.local"))!!) {
                return ingress
            }
        }

        throw IllegalStateException("Kunne ikke fastslå ingress til tjeneste!")
    }

    private fun readWithGson(jsonPath: String): Map<String, Any> {
        val bufferedReader = BufferedReader(FileReader(jsonPath))
        val gson = Gson()

        @Suppress("UNCHECKED_CAST")
        return gson.fromJson(bufferedReader, Map::class.java) as Map<String, Any>
    }

    private fun determineAppklicationName(applicationNameOrAlias: String): String {
        if (ALIAS_TO_NAIS_APPLICATION.containsKey(applicationNameOrAlias)) {
            return ALIAS_TO_NAIS_APPLICATION.getValue(applicationNameOrAlias)
        }

        if (Environment.isApplicationPresentInNaisProjectFolder(applicationNameOrAlias)) {
            return applicationNameOrAlias
        }

        val message = "Unable to determine application name for '$applicationNameOrAlias'"
        LOGGER.error(message)
        throw IllegalStateException(message)
    }
}
