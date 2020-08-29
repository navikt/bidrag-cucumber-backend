package no.nav.bidrag.cucumber

import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.springframework.http.HttpHeaders
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import java.security.cert.X509Certificate

internal class CacheRestTemplateMedBaseUrl {
    companion object {
        private val restTjenesteTilApplikasjon: MutableMap<String, RestTjeneste.RestTemplateMedBaseUrl> = HashMap()
        private val environment = Environment()
        private val naisConfiguration = NaisConfiguration()
    }

    fun hentEllerKonfigurer(applicationOrAlias: String): RestTjeneste.RestTemplateMedBaseUrl {
        val applicationName = naisConfiguration.readNaisConfiguration(applicationOrAlias)

        if (restTjenesteTilApplikasjon.containsKey(applicationName)) {
            return restTjenesteTilApplikasjon.getValue(applicationName)
        }

        val appliationUrl = naisConfiguration.hentApplicationHostUrl(applicationName) + applicationName + '/'

        return hentEllerKonfigurerApplikasjonForUrl(applicationName, appliationUrl)
    }

    fun hentEllerKonfigurer(applicationOrAlias: String, applicationContext: String): RestTjeneste.RestTemplateMedBaseUrl {
        val applicationName = naisConfiguration.readNaisConfiguration(applicationOrAlias)

        if (restTjenesteTilApplikasjon.containsKey(applicationName)) {
            return restTjenesteTilApplikasjon.getValue(applicationName)
        }

        val applicationUrl = naisConfiguration.hentApplicationHostUrl(applicationName) + bestemApplicationContextPath(applicationContext)

        return hentEllerKonfigurerApplikasjonForUrl(applicationName, applicationUrl)
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
        val httpHeaderRestTemplate = environment.setBaseUrlPa(HttpHeaderRestTemplate(httpComponentsClientHttpRequestFactory), applicationUrl)

        httpHeaderRestTemplate.addHeaderGenerator(HttpHeaders.AUTHORIZATION) { Sikkerhet().fetchIdToken() }

        restTjenesteTilApplikasjon[applicationOrAlias] = RestTjeneste.RestTemplateMedBaseUrl(httpHeaderRestTemplate, applicationUrl)

        return restTjenesteTilApplikasjon[applicationOrAlias]!!
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
