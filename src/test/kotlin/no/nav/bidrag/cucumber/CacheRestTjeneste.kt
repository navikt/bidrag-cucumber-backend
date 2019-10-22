package no.nav.bidrag.cucumber

class CacheRestTjeneste {
    companion object {
        private val restTjenester = HashMap<String, RestTjeneste>()

        internal fun hent(alias: String): RestTjeneste {
            if (!restTjenester.containsKey(alias)) {
                restTjenester[alias] = RestTjeneste(alias)
            }

            return restTjenester[alias]!!
        }
    }
}