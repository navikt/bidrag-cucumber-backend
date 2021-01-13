package no.nav.bidrag.cucumber

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

internal class NaisConfiguration {
    companion object {
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

        private val envFilePathPerAppName: MutableMap<String, String> = HashMap()

        internal fun determineAppklicationName(applicationNameOrAlias: String): String {
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

    fun readNaisConfiguration(applicationOrAlias: String): String {
        val applicationName = determineAppklicationName(applicationOrAlias)

        if (!envFilePathPerAppName.containsKey(applicationName)) {
            fetchNaisConfiguration(applicationName)
        }

        return applicationName
    }

    private fun fetchNaisConfiguration(applicationName: String) {
        val applfolder = File("${Environment.naisProjectFolder}/$applicationName")
        val naisFolder = File("${Environment.naisProjectFolder}/$applicationName/nais")
        val envFile = fetchEnvFileByEnvironment(applicationName)

        LOGGER.info("> applFolder exists: ${applfolder.exists()}, path: $applfolder")
        LOGGER.info("> naisFolder exists: ${naisFolder.exists()}, path: $naisFolder")
        LOGGER.info("> envFile    exists: ${envFile.exists()}, path: $envFile")

        val canReadNaisEnvironment = applfolder.exists() && naisFolder.exists() && envFile.exists()

        if (canReadNaisEnvironment) {
            envFilePathPerAppName[applicationName] = envFile.absolutePath
            namespaceJsonFilePathPerAppName[applicationName] = jsonFile.absolutePath
            Sikkerhet.SECURITY_PER_APPLIKASJON.computeIfAbsent(applicationName) { hentAzureSomSecurityToken(jsonFile.parent) ?: Security.ISSO }
        }
    }

    private fun hentAzureSomSecurityToken(naisFolder: String): Security? {
        val naisYamlReader = File(naisFolder, "nais.yaml").bufferedReader()
        val pureYaml = mutableListOf<String>()
        naisYamlReader.useLines { lines -> lines.forEach { if (!it.contains("{{")) pureYaml.add(it) } }
        val yamlMap = Yaml().load<Map<String, Any>>(pureYaml.joinToString("\n"))

        return if (isEnabled(yamlMap, mutableListOf("spec", "azure", "application", "enabled"))) Security.AZURE else null
    }

    private fun isEnabled(map: Map<String, Any>, keys: MutableList<String>): Boolean {
        println("${keys[0]}=${map[keys[0]]}")

        if (map.containsKey(keys[0])) {
            if (keys.size == 1) return map.getValue(keys[0]) as Boolean
            else {
                @Suppress("UNCHECKED_CAST")
                val childMap = map[keys[0]] as Map<String, Any>
                keys.removeAt(0)
                return isEnabled(childMap, keys)
            }
        }

        return false
    }

    private fun fetchEnvFileByEnvironment(applicationName: String): File {
        val miljoJson = File("${Environment.naisProjectFolder}/$applicationName/nais/${Environment.miljo}.json")

        if (miljoJson.exists()) {
            return miljoJson
        }

        val miljoYaml = File("${Environment.naisProjectFolder}/$applicationName/nais/${Environment.miljo}.yaml")

        if (miljoYaml.exists()) {
            return miljoYaml
        }

        throw IllegalStateException(" Unable to find ${Environment.naisProjectFolder}/$applicationName/nais/${Environment.namespace}.? (json or yaml)")
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
}
