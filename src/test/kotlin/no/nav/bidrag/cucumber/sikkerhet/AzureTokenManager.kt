package no.nav.bidrag.cucumber.sikkerhet

import no.nav.bidrag.cucumber.Environment
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

internal object AzureTokenManager {

    private val LOGGER = LoggerFactory.getLogger(AzureTokenManager::class.java)

    fun fetchToken(applicationName: String): String {
        val integrationInput = Environment.fetchIntegrationInput()
        val azureInput = integrationInput.fetchAzureInput(applicationName)
        val azureAdUrl = "${azureInput.authorityEndpoint}/${azureInput.tenant}/oauth2/v2.0/token"
        val httpHeaders = HttpHeaders()
        val restTemplate = RestTemplate()

        httpHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        map.add("client_id", azureInput.clientId)
        map.add("client_secret", azureInput.clientSecret)
        map.add("grant_type", "password")
        map.add("scope", "openid ${azureInput.clientId}/.default")
        map.add("username", integrationInput.fetchTenantUsername())
        map.add("password", Environment.fetchTestAuthentication())

        LOGGER.info("> url    : $azureAdUrl")
        LOGGER.info("> headers: $httpHeaders")
        LOGGER.info("> map    : $map")

        val request = HttpEntity(map, httpHeaders)
        val token = restTemplate.postForEntity(azureAdUrl, request, AzureToken::class.java).body
            ?: throw IllegalStateException("Klarte ikke å hente token fra $azureAdUrl")

        LOGGER.info("Fetched azure token for ${integrationInput.fetchTenantUsername()}")

        return "Bearer ${token.token}"
    }
}