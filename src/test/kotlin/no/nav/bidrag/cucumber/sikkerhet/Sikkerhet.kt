package no.nav.bidrag.cucumber.sikkerhet

import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.NaisConfiguration
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.charset.StandardCharsets

object Sikkerhet {

    private val LOGGER = LoggerFactory.getLogger(Sikkerhet::class.java)
    private val SECURITY_FOR_APPLICATION: MutableMap<String, Security> = HashMap()

    internal fun fetchToken(applicationName: String): String {
        try {
            return when (SECURITY_FOR_APPLICATION[applicationName]) {
                Security.ISSO -> IssoTokenManager.fetchOnlineIdToken(Environment.namespace)
                Security.AZURE -> AzureTokenManager.fetchToken(applicationName)
                else -> IssoTokenManager.fetchOnlineIdToken(Environment.namespace)
            }
        } catch (e: RuntimeException) {
            val exception = "${e.javaClass.name}: ${e.message} - ${e.stackTrace.first { it.fileName != null && it.fileName!!.endsWith("kt") }}"
            LOGGER.error("Feil ved henting av token, $exception")
            throw e
        }
    }

    internal fun base64EncodeCredentials(username: String, password: String): String {
        val credentials = "$username:$password"

        val encodedCredentials: ByteArray = java.util.Base64.getEncoder().encode(credentials.toByteArray())

        return String(encodedCredentials, StandardCharsets.UTF_8)
    }

    internal fun settOpp(environmentFile: NaisConfiguration.EnvironmentFile) {
        var security = if (harAzureSomSikkerhet(environmentFile.parentFile)) Security.AZURE else Security.ISSO

        if (skalEndreTilIsso(environmentFile.applicationName)) {
            security = Security.ISSO
        }

        SECURITY_FOR_APPLICATION[environmentFile.applicationName] = security
    }

    private fun harAzureSomSikkerhet(naisFolder: File): Boolean {
        val lines = File(naisFolder, "nais.yaml").readLines(Charsets.UTF_8)
        val pureYaml = mutableListOf<String>()
        lines.forEach { if (!it.contains("{{")) pureYaml.add(it) }
        val yamlMap = Yaml().load<Map<String, Any>>(pureYaml.joinToString("\n"))

        return isEnabled(yamlMap, mutableListOf("spec", "azure", "application", "enabled"))
    }

    private fun isEnabled(map: Map<String, Any>, keys: MutableList<String>): Boolean {
        LOGGER.info("> key=value  to use: ${keys[0]}=${map[keys[0]]}")

        if (map.containsKey(keys[0])) {
            return if (keys.size == 1) {
                map.getValue(keys[0]) as Boolean
            } else {
                @Suppress("UNCHECKED_CAST")
                val childMap = map[keys[0]] as Map<String, Any>
                keys.removeAt(0)
                isEnabled(childMap, keys)
            }
        }

        return false
    }

    @Suppress("UNUSED_PARAMETER")
    private fun skalEndreTilIsso(applicationName: String) = true // foreløpig ikke noe gyldig azure token

    private enum class Security {
        AZURE, ISSO
    }
}
