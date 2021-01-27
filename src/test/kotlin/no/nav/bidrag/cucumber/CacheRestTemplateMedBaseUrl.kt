package no.nav.bidrag.cucumber

import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.cucumber.sikkerhet.Fasit
import no.nav.bidrag.cucumber.sikkerhet.Sikkerhet
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import java.security.cert.X509Certificate

internal object CacheRestTemplateMedBaseUrl {

    private val LOGGER = LoggerFactory.getLogger(CacheRestTemplateMedBaseUrl::class.java)
    private val REST_TJENESTE_TIL_APPLIKASJON: MutableMap<String, RestTjeneste.RestTemplateMedBaseUrl> = HashMap()

    fun hentEllerKonfigurer(applicationOrAlias: String): RestTjeneste.RestTemplateMedBaseUrl {
        val applicationName = NaisConfiguration.read(applicationOrAlias)

        return REST_TJENESTE_TIL_APPLIKASJON.computeIfAbsent(applicationName) { konfigurer(applicationName) }
    }

    fun hentEllerKonfigurer(alias: String, fasitRessurs: Fasit.FasitRessurs): RestTjeneste.RestTemplateMedBaseUrl {
        return REST_TJENESTE_TIL_APPLIKASJON.computeIfAbsent(alias) { konfigurerApplikasjonForUrl(alias, fasitRessurs.url())}
    }

    fun hentEllerKonfigurer(applicationOrAlias: String, applicationContext: String): RestTjeneste.RestTemplateMedBaseUrl {
        val applicationName = NaisConfiguration.read(applicationOrAlias)

        return REST_TJENESTE_TIL_APPLIKASJON.computeIfAbsent(applicationName) { konfigurerApplikasjonForContext(applicationName, applicationContext) }
    }

    private fun konfigurer(applicationName: String): RestTjeneste.RestTemplateMedBaseUrl {
        val applicationHostUrl = NaisConfiguration.hentApplicationHostUrl(applicationName)
        val applicationUrl = joinUrlAndContextWithoutDoubleSlash(applicationHostUrl, applicationName)

        return konfigurerApplikasjonForUrl(applicationName, applicationUrl)
    }

    private fun konfigurerApplikasjonForUrl(applicationName: String, applicationUrl: String): RestTjeneste.RestTemplateMedBaseUrl {

        val httpComponentsClientHttpRequestFactory = hentHttpRequestFactorySomIgnorererHttps()
        val httpHeaderRestTemplate = RestTjeneste.setBaseUrlPa(HttpHeaderRestTemplate(httpComponentsClientHttpRequestFactory), applicationUrl)

        httpHeaderRestTemplate.addHeaderGenerator(HttpHeaders.AUTHORIZATION) { Sikkerhet.fetchToken(applicationName) }

        return RestTjeneste.RestTemplateMedBaseUrl(httpHeaderRestTemplate, applicationUrl)
    }

    private fun konfigurerApplikasjonForContext(applicationName: String, applicationContext: String): RestTjeneste.RestTemplateMedBaseUrl {
        val applicationHostUrl = NaisConfiguration.hentApplicationHostUrl(applicationName)
        val applicationContextPath = bestemApplicationContextPath(applicationContext)

        LOGGER.info("Bruker '$applicationHostUrl' sammen med '$applicationContextPath' for å bestemme host url")

        val applicationUrl = joinUrlAndContextWithoutDoubleSlash(applicationHostUrl, applicationContextPath)

        return konfigurerApplikasjonForUrl(applicationName, applicationUrl)
    }

    private fun joinUrlAndContextWithoutDoubleSlash(applicationHostUrl: String, applicationContextPath: String): String {
        return if (!applicationHostUrl.endsWith('/') && !applicationContextPath.startsWith('/')) {
            "$applicationHostUrl/$applicationContextPath"
        } else {
            "$applicationHostUrl$applicationContextPath"
        }
    }

    private fun bestemApplicationContextPath(applicationContext: String): String {
        return if (applicationContext == "/") {
            ""
        } else {
            "$applicationContext/"
        }
    }

    private fun hentHttpRequestFactorySomIgnorererHttps(): HttpComponentsClientHttpRequestFactory {
        val acceptingTrustStrategy = { _: Array<X509Certificate>, _: String -> true }
        val sslContext = SSLContexts.custom()
            .loadTrustMaterial(null, acceptingTrustStrategy)
            .build()

        val csf = SSLConnectionSocketFactory(sslContext)

        val httpClient = HttpClients.custom()
            .setSSLSocketFactory(csf)
            .build()

        val requestFactory = HttpComponentsClientHttpRequestFactory()

        requestFactory.httpClient = httpClient

        return requestFactory
    }
}
