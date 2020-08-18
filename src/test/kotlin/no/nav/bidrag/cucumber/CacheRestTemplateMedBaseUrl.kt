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
        private val cache: MutableMap<String, RestTjeneste.RestTemplateMedBaseUrl> = HashMap()
        private val environment = Environment()
        private val fasit = Fasit()
        private val simpleNaisConfiguration = NaisConfiguration()
    }

    fun hentEllerKonfigurer(applicationContext: String): RestTjeneste.RestTemplateMedBaseUrl {
        if (cache.containsKey(applicationContext)) {
            return cache[applicationContext]!!
        }

        val appliationUrl = when (simpleNaisConfiguration.supports(applicationContext)) {
            true -> simpleNaisConfiguration.hentApplicationHostUrl(applicationContext) + applicationContext + '/'
            false -> fasit.hentFullContextPath(applicationContext)
        }

        return hentEllerKonfigurerApplikasjonForUrl(applicationContext, appliationUrl);
    }

    fun hentEllerKonfigurer(applicationOrAlias: String, applicationContext: String): RestTjeneste.RestTemplateMedBaseUrl {
        if (cache.containsKey(applicationOrAlias)) {
            return cache[applicationOrAlias]!!
        }

        simpleNaisConfiguration.supports(applicationOrAlias)
        val applicationUrl = simpleNaisConfiguration.hentApplicationHostUrl(applicationOrAlias) + bestemApplicationContextPath(applicationContext);

        return hentEllerKonfigurerApplikasjonForUrl(applicationOrAlias, applicationUrl)
    }


    private fun bestemApplicationContextPath(applicationContext: String): String {
        var applicationContextPath: String
        if (!applicationContext.isEmpty() && applicationContext.equals("/")) {
            applicationContextPath = ""
        } else {
            applicationContextPath = applicationContext + "/"
        }
        return applicationContextPath
    }

    fun hentEllerKonfigurerApplikasjonForUrl(applicationOrAlias: String, applicationUrl: String): RestTjeneste.RestTemplateMedBaseUrl {

        val httpComponentsClientHttpRequestFactory = hentHttpRequestFactorySomIgnorererHttps()
        val httpHeaderRestTemplate = environment.setBaseUrlPa(HttpHeaderRestTemplate(httpComponentsClientHttpRequestFactory), applicationUrl)

        httpHeaderRestTemplate.addHeaderGenerator(HttpHeaders.AUTHORIZATION) { Sikkerhet().fetchIdToken() }

        cache[applicationOrAlias] = RestTjeneste.RestTemplateMedBaseUrl(httpHeaderRestTemplate, applicationUrl)

        return cache[applicationOrAlias]!!
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
