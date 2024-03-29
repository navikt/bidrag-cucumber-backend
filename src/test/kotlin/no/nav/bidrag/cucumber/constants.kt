package no.nav.bidrag.cucumber

// constants for input via System.getProperty
internal const val CREDENTIALS_PIP_AUTH = "PIP_AUTH"
internal const val CREDENTIALS_TEST_USER = "TEST_USER"
internal const val CREDENTIALS_TEST_USER_AUTH = "TEST_AUTH"
internal const val CREDENTIALS_USERNAME = "USERNAME"
internal const val CREDENTIALS_USER_AUTH = "USER_AUTH"
internal const val ENVIRONMENT = "ENVIRONMENT"
internal const val INTEGRATION_INPUT: String = "INTEGRATION_INPUT"
internal const val PROJECT_NAIS_FOLDER = "PROJECT_NAIS_FOLDER"

// constants for code
internal const val ALIAS_BIDRAG_UI = "bidrag-ui"
internal const val ALIAS_OIDC = "$ALIAS_BIDRAG_UI-oidc"
internal const val FASIT_ZONE = "fss"

// Urls
internal const val URL_FASIT = "https://fasit.adeo.no/api/v2/resources"
internal const val URL_ISSO = "https://isso-q.adeo.no:443/isso/json/authenticate?authIndexType=service&authIndexValue=ldapservice"
internal const val URL_ISSO_AUTHORIZE = "https://isso-q.adeo.no/isso/oauth2/authorize"
internal const val URL_ISSO_ACCESS_TOKEN = "https://isso-q.adeo.no:443/isso/oauth2/access_token"

// Headers
internal const val X_ENHET_HEADER = "X-Enhet"
internal const val X_OPENAM_PASSW_HEADER = "X-OpenAM-Password"
internal const val X_OPENAM_USER_HEADER = "X-OpenAM-Username"
