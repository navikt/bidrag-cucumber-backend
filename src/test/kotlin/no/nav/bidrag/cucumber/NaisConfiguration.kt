package no.nav.bidrag.cucumber

import com.google.gson.Gson
import no.nav.bidrag.cucumber.input.IntegrationInput
import no.nav.bidrag.cucumber.sikkerhet.Sikkerhet
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.File

internal object NaisConfiguration {

    private val LOGGER = LoggerFactory.getLogger(NaisConfiguration::class.java)
    private val ENVIRONMENT_FOR_APPLICATION: MutableMap<String, EnvironmentFile> = HashMap()
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

    fun read(applicationOrAlias: String): String {
        val applicationName = determineAppklicationName(applicationOrAlias)

        if (!ENVIRONMENT_FOR_APPLICATION.containsKey(applicationName)) {
            readNaisConfiguration(applicationName)
        }

        return applicationName
    }

    private fun readNaisConfiguration(applicationName: String) {
        val integrationInput = Environment.fetchIntegrationInput()
        val applfolder = File("${integrationInput.naisProjectFolder}/$applicationName")
        val naisFolder = File("${integrationInput.naisProjectFolder}/$applicationName/nais")
        val envFile = fetchEnvFileByEnvironment(applicationName, integrationInput)

        LOGGER.info("> applFolder exists: ${applfolder.exists()}, path: $applfolder")
        LOGGER.info("> naisFolder exists: ${naisFolder.exists()}, path: $naisFolder")
        LOGGER.info("> envFile    exists: ${envFile.exists()}, path: $envFile")

        val canReadNaisEnvironment = applfolder.exists() && naisFolder.exists() && envFile.exists()

        if (canReadNaisEnvironment) {
            val environmentFile = EnvironmentFile(applicationName, envFile)
            ENVIRONMENT_FOR_APPLICATION[applicationName] = environmentFile
            Sikkerhet.settOpp(environmentFile)
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

        throw IllegalStateException("Unable to find ${integrationInput.naisProjectFolder}/$applicationName/nais/${integrationInput.environment}.? (json or yaml)")
    }

    internal fun hentApplicationHostUrl(applicationNameOrAlias: String): String {

        val applicationName = determineAppklicationName(applicationNameOrAlias)
        val environmentFile = ENVIRONMENT_FOR_APPLICATION[applicationName]
            ?: throw IllegalStateException("no path for $applicationName in $ENVIRONMENT_FOR_APPLICATION")

        val ingresses = if (environmentFile.endsWith(".yaml")) {
            fetchIngressesFromYaml(environmentFile)
        } else {
            fetchIngressesFromJson(environmentFile)
        }

        var ingress = fetchIngress(ingresses).replace("//", "/").replace("https:/", "https://")

        if (ingress.endsWith("/")) {
            ingress = ingress.removeSuffix("/")
        }

        return ingress
    }

    private fun fetchIngressesFromYaml(environmentFile: EnvironmentFile): List<String> {
        val yamlReader = environmentFile.bufferedReader()
        val yamlMap = Yaml().load<Map<String, List<String>>>(yamlReader)

        return yamlMap.getValue("ingresses")
    }

    private fun fetchIngressesFromJson(environmentFile: EnvironmentFile): List<String> {
        val jsonFileAsMap = environmentFile.readWithGson()

        for (json in jsonFileAsMap) {
            println(json)
        }

        @Suppress("UNCHECKED_CAST") return jsonFileAsMap["ingresses"] as List<String>
    }

    private fun fetchIngress(ingresses: List<String?>): String {
        return fetchIngress(ingresses, "dev.adeo")
            ?: fetchIngress(ingresses, "preprod.local")
            ?: throw IllegalStateException("Kunne ikke fastslå ingress til tjeneste!")
    }

    private fun fetchIngress(ingresses: List<String?>, hostName: String): String? {
        for (ingress in ingresses) {
            if (ingress?.contains(Regex(hostName))!!) {
                return ingress
            }
        }

        return null
    }

    private fun determineAppklicationName(applicationNameOrAlias: String): String {
        if (ALIAS_TO_NAIS_APPLICATION.containsKey(applicationNameOrAlias)) {
            return ALIAS_TO_NAIS_APPLICATION.getValue(applicationNameOrAlias)
        }

        if (Environment.naisApplications.contains(applicationNameOrAlias)) {
            return applicationNameOrAlias
        }

        val message = "Unable to determine application name for '$applicationNameOrAlias'"
        LOGGER.error(message)
        throw IllegalStateException(message)
    }

    internal data class EnvironmentFile(
        val applicationName: String,
        val naisEnvironmentFile: File
    ) {
        val parentFile: File
            get() = naisEnvironmentFile.parentFile

        fun endsWith(suffix: String): Boolean {
            return naisEnvironmentFile.absolutePath.endsWith(suffix)
        }

        fun bufferedReader() = naisEnvironmentFile.bufferedReader(Charsets.UTF_8)
        fun readWithGson(): Map<String, Any> {
            val bufferedReader = bufferedReader()
            val gson = Gson()

            @Suppress("UNCHECKED_CAST")
            return gson.fromJson(bufferedReader, Map::class.java) as Map<String, Any>
        }
    }
}
