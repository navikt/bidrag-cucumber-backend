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

    fun hentEllerKonfigurer(applicationOrAlias: String): RestTjeneste.RestTemplateMedBaseUrl {
        if (cache.containsKey(applicationOrAlias)) {
            return cache[applicationOrAlias]!!
        }

        val fullContextPath = when (simpleNaisConfiguration.supports(applicationOrAlias)) {
            true -> simpleNaisConfiguration.hentFullContextPath(applicationOrAlias)
            false -> fasit.hentFullContextPath(applicationOrAlias)
        }

        val httpComponentsClientHttpRequestFactory = hentHttpRequestFactorySomIgnorererHttps()
        val httpHeaderRestTemplate = environment.setBaseUrlPa(HttpHeaderRestTemplate(httpComponentsClientHttpRequestFactory), fullContextPath)

        httpHeaderRestTemplate.addHeaderGenerator(HttpHeaders.AUTHORIZATION) { Sikkerhet().fetchIdToken() }
        cache[applicationOrAlias] = RestTjeneste.RestTemplateMedBaseUrl(httpHeaderRestTemplate, fullContextPath)

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
