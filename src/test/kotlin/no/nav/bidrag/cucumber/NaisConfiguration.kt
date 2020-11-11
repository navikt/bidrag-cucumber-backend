package no.nav.bidrag.cucumber

import com.google.gson.Gson
import org.slf4j.LoggerFactory
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
        private val namespaceJsonFilePathPerAppName: MutableMap<String, String> = HashMap()

    }

    private val fetchResourceFromFasit: MutableSet<String> = HashSet()

    fun readNaisConfiguration(applicationOrAlias: String): String {
        val applicationName = determineAppklicationName(applicationOrAlias)

        if (!namespaceJsonFilePathPerAppName.containsKey(applicationName)) {
            fetchNaisConfiguration(applicationName)
        }

        return applicationName
    }

    private fun fetchNaisConfiguration(applicationName: String) {
        val applfolder = File("${Environment.naisProjectFolder}/$applicationName")
        val naisFolder = File("${Environment.naisProjectFolder}/$applicationName/nais")
        val jsonFile = fetchJsonByEnvironmentOrNamespace(applicationName)

        LOGGER.info("> applFolder exists: ${applfolder.exists()}, path: $applfolder")
        LOGGER.info("> naisFolder exists: ${naisFolder.exists()}, path: $naisFolder")
        LOGGER.info("> jsonFile   exists: ${jsonFile.exists()}, path: $jsonFile")

        val canReadNaisJson = applfolder.exists() && naisFolder.exists() && jsonFile.exists()

        if (canReadNaisJson) {
            namespaceJsonFilePathPerAppName[applicationName] = jsonFile.absolutePath
        } else {
            fetchResourceFromFasit.add(applicationName)
        }
    }

    private fun fetchJsonByEnvironmentOrNamespace(applicationName: String): File {
        val miljoJson = File("${Environment.naisProjectFolder}/$applicationName/nais/${Environment.miljo}.json")

        if (miljoJson.exists()) {
            return miljoJson
        } else {
            LOGGER.warn("Unable to find ${Environment.miljo}.json, using ${Environment.namespace}.json")
        }

        return File("${Environment.naisProjectFolder}/$applicationName/nais/${Environment.namespace}.json")
    }

    internal fun hentApplicationHostUrl(applicationNameOrAlias: String): String {

        val applicationName = determineAppklicationName(applicationNameOrAlias)
        val nameSpaceJsonFile = namespaceJsonFilePathPerAppName[applicationName]
                ?: throw IllegalStateException("no path for $applicationName in $namespaceJsonFilePathPerAppName")

        val jsonFileAsMap = readWithGson(nameSpaceJsonFile)
        var ingress = jsonFileAsMap["ingress_preprod"]

        if (ingress == null) {
            for (json in jsonFileAsMap) {
                println(json)
            }

            @Suppress("UNCHECKED_CAST") val ingresses = jsonFileAsMap["ingresses"] as List<String>
            ingress = fetchIngress(ingresses)
        }

        return "$ingress".replace("//", "/").replace("https:/", "https://")
    }

    private fun fetchIngress(ingresses: List<String?>): String? {

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
