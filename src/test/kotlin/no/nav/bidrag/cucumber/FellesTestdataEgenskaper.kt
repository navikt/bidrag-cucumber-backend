package no.nav.bidrag.cucumber

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.dokument.RestTjenesteTestdata
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus

class FellesTestdataEgenskaper {

    companion object {
        private lateinit var key: String
        private lateinit var restTjenesteTestdata: RestTjenesteTestdata
        val journalpostIdPerKey: MutableMap<String, String> = HashMap()

        fun useAsKey(cacheKey: String) {
            key = cacheKey
        }
    }

    @Gitt("resttjenesten {string} for manipulering av testdata")
    fun resttjenesten(alias: String) {
        restTjenesteTestdata = RestTjenesteTestdata(alias)
    }

    @Og("endre journalpost med id {string} til:")
    fun `endre journalpost med id til`(journalpostId: String, json: String) {
        restTjenesteTestdata.put("/journalpost/$journalpostId", json)
    }

    @Og("opprett journalpost når den ikke finnes for nøkkel:")
    fun `opprett journalpost`(jpJson: String) {
        if (!journalpostIdPerKey.containsKey(key)) {
            restTjenesteTestdata.opprettJournalpost(jpJson)
            assertThat(restTjenesteTestdata.hentHttpStatus()).isEqualTo(HttpStatus.CREATED)

            val opprettetJpMap = restTjenesteTestdata.hentResponseSomMap()
            journalpostIdPerKey[key] = opprettetJpMap["journalpostId"] as String
        }
    }
}
