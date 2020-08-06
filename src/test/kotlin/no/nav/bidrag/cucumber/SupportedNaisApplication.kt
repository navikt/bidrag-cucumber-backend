package no.nav.bidrag.cucumber

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

internal class SupportedNaisApplication {
    private val nameIsSupported: MutableMap<String, Boolean> = HashMap()
    private val namespaceJsonFilePathPerAppName: MutableMap<String, String> = HashMap()
    private var naisConfigurationFolder: String? = null

    fun isSupported(applicationName: String): Boolean {
        if (nameIsSupported.containsKey(applicationName)) {
            return nameIsSupported[applicationName]!!
        }

        if (naisConfigurationFolder == null) {
            naisConfigurationFolder = System.getProperty(PROJECT_NAIS_FOLDER)

            if (naisConfigurationFolder == null || !File("$naisConfigurationFolder").exists()) {
                val message = "Fant ikke mappe som holder alle nais applikasjoner! Property: $PROJECT_NAIS_FOLDER, verdi: $naisConfigurationFolder"
                throw IllegalStateException(message)
            }
        }

        putSupportStatus(applicationName)

        return nameIsSupported[applicationName]!!
    }

    private fun putSupportStatus(applicationName: String) {
        val applfolder = File("$naisConfigurationFolder/$applicationName")
        val naisFolder = File("$naisConfigurationFolder/$applicationName/nais")
        val jsonFile = File("$naisConfigurationFolder/$applicationName/nais/${Environment.fetch()}.json")

        println("> applFolder exists: ${applfolder.exists()}, path: $applfolder")
        println("> naisFolder exists: ${naisFolder.exists()}, path: $naisFolder")
        println("> jsonFile   exists: ${jsonFile.exists()}, path: $jsonFile")

        val isSupported = applfolder.exists() && naisFolder.exists() && jsonFile.exists()
        nameIsSupported[applicationName] = isSupported

        if (isSupported) {
            namespaceJsonFilePathPerAppName[applicationName] = jsonFile.absolutePath
        }
    }

    /**
     * before calling this method, isSupported(applicationName) must be true
     */
    internal fun fetchFullContextPath(applicationName: String): String {

        val jsonFileAsMap = readWithGson(namespaceJsonFilePathPerAppName[applicationName]!!)
        val ingressPreprod = jsonFileAsMap["ingress_preprod"]

        return "${ingressPreprod}$applicationName".replace("//", "/").replace("https:/", "https://")
    }

    private fun readWithGson(jsonPath: String): Map<String, String> {
        val bufferedReader = BufferedReader(FileReader(jsonPath))
        val gson = Gson()

        @Suppress("UNCHECKED_CAST")
        return gson.fromJson(bufferedReader, Map::class.java) as Map<String, String>
    }
}
