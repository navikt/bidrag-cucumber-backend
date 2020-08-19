package no.nav.bidrag.cucumber

internal class NaisConfiguration {
    companion object {
        private var supportedNaisApplication = SupportedNaisApplication()
    }

    internal fun supports(applicationName: String) = supportedNaisApplication.isSupported(applicationName)

    internal fun hentApplicationHostUrl(applicationName: String): String {
        if (Environment.offline) {
            return "http://localhost:8080"
        }

        return supportedNaisApplication.fetchApplicationHostUrl(applicationName)
    }
}
