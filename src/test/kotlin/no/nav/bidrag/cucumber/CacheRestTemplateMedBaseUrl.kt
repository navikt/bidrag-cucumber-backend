package no.nav.bidrag.cucumber

import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import java.security.cert.X509Certificate

private val LOGGER = LoggerFactory.getLogger(CacheRestTemplateMedBaseUrl::class.java)
private val REST_TJENESTE_TIL_APPLIKASJON: MutableMap<String, RestTjeneste.RestTemplateMedBaseUrl> = HashMap()

internal object CacheRestTemplateMedBaseUrl {

    fun hentEllerKonfigurer(applicationOrAlias: String): RestTjeneste.RestTemplateMedBaseUrl {
        val applicationName = NaisConfiguration.readNaisConfiguration(applicationOrAlias)

        if (REST_TJENESTE_TIL_APPLIKASJON.containsKey(applicationName)) {
            return REST_TJENESTE_TIL_APPLIKASJON.getValue(applicationName)
        }

        val applicationHostUrl = NaisConfiguration.hentApplicationHostUrl(applicationName)
        val applicationUrl = joinUrlAndContextWithoutDoubleSlash(applicationHostUrl, applicationName)

        return hentEllerKonfigurerApplikasjonForUrl(applicationName, applicationUrl)
    }

    fun hentEllerKonfigurer(applicationOrAlias: String, applicationContext: String): RestTjeneste.RestTemplateMedBaseUrl {
        val applicationName = NaisConfiguration.readNaisConfiguration(applicationOrAlias)

        if (REST_TJENESTE_TIL_APPLIKASJON.containsKey(applicationName)) {
            return REST_TJENESTE_TIL_APPLIKASJON.getValue(applicationName)
        }

        val applicationHostUrl = NaisConfiguration.hentApplicationHostUrl(applicationName)
        val applicationContextPath = bestemApplicationContextPath(applicationContext)

        LOGGER.info("Bruker '$applicationHostUrl' sammen med '$applicationContextPath' for å bestemme host url")

        val applicationUrl = joinUrlAndContextWithoutDoubleSlash(applicationHostUrl, applicationContextPath)

        return hentEllerKonfigurerApplikasjonForUrl(applicationName, applicationUrl)
    }

    private fun joinUrlAndContextWithoutDoubleSlash(applicationHostUrl: String, applicationContextPath: String): String {
        return if (!applicationHostUrl.endsWith('/') && !applicationContextPath.startsWith('/')) {
            "$applicationHostUrl/$applicationContextPath"
        } else {
            "$applicationHostUrl$applicationContextPath"
        }
    }

    fun hentEllerKonfigurer(alias: String, fasitRessurs: Fasit.FasitRessurs): RestTjeneste.RestTemplateMedBaseUrl {
        return hentEllerKonfigurerApplikasjonForUrl(alias, fasitRessurs.url())
    }

    private fun bestemApplicationContextPath(applicationContext: String): String {
        return if (applicationContext == "/") {
            ""
        } else {
            "$applicationContext/"
        }
    }

    private fun hentEllerKonfigurerApplikasjonForUrl(applicationOrAlias: String, applicationUrl: String): RestTjeneste.RestTemplateMedBaseUrl {

        val httpComponentsClientHttpRequestFactory = hentHttpRequestFactorySomIgnorererHttps()
        val httpHeaderRestTemplate = Environment.setBaseUrlPa(HttpHeaderRestTemplate(httpComponentsClientHttpRequestFactory), applicationUrl)

        httpHeaderRestTemplate.addHeaderGenerator(HttpHeaders.AUTHORIZATION) { Sikkerhet().fetchIdToken() }

        REST_TJENESTE_TIL_APPLIKASJON[applicationOrAlias] = RestTjeneste.RestTemplateMedBaseUrl(httpHeaderRestTemplate, applicationUrl)

        return REST_TJENESTE_TIL_APPLIKASJON.getValue(applicationOrAlias)
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
