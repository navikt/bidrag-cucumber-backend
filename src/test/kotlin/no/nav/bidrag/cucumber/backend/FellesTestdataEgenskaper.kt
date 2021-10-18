package no.nav.bidrag.cucumber.backend

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Og
import no.nav.bidrag.cucumber.backend.dokument.AvvikDataMottaksregistrertJp
import no.nav.bidrag.cucumber.backend.dokument.RestTjenesteTestdata
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

    @Og("opprett journalpost når den ikke finnes for nøkkel:")
    fun `opprett journalpost`(jpJson: String) {
        if (!journalpostIdPerKey.containsKey(key)) {
            restTjenesteTestdata.opprettJournalpost(jpJson)
            assertThat(restTjenesteTestdata.hentHttpStatus()).isEqualTo(HttpStatus.CREATED)

            val opprettetJpMap = restTjenesteTestdata.hentResponseSomMap()
            journalpostIdPerKey[key] = opprettetJpMap["journalpostId"] as String
        }
    }

    @Og("opprett journalpost med nøkkel {string} når den ikke finnes:")
    fun `opprett journalpost med nokkel`(nokkel: String, jpJson: String) {
        if (!journalpostIdPerKey.containsKey(nokkel)) {
            restTjenesteTestdata.opprettJournalpost(jpJson)
            assertThat(restTjenesteTestdata.hentHttpStatus()).isEqualTo(HttpStatus.CREATED)

            val opprettetJpMap = restTjenesteTestdata.hentResponseSomMap()
            journalpostIdPerKey[nokkel] = opprettetJpMap["journalpostId"] as String
        }
    }

    @Og("opprettet journalpost har enhet {string}")
    fun `opprettet journalpost har enhet`(enhet: String) {
        assertThat(journalpostIdPerKey["jouranlforendeEnhet"]).isEqualTo(enhet)
    }

    @Gitt("at jeg oppretter journalpost for {string}:")
    fun `opprett journalpost`(key: String, jpJson: String) {
        if (!journalpostIdPerKey.containsKey(key)) {
            restTjenesteTestdata.opprettJournalpost(jpJson)
            assertThat(restTjenesteTestdata.hentHttpStatus()).isEqualTo(HttpStatus.CREATED)

            val opprettetJpMap = restTjenesteTestdata.hentResponseSomMap()
            journalpostIdPerKey[key] = opprettetJpMap["journalpostId"] as String
        }
    }

    fun `opprett journalpost`(avvikData: AvvikDataMottaksregistrertJp, jpJson: String) {
        if (!journalpostIdPerKey.containsKey(avvikData.testdataNokkel)) {
            restTjenesteTestdata.opprettJournalpost(jpJson)
            assertThat(restTjenesteTestdata.hentHttpStatus()).isEqualTo(HttpStatus.CREATED)

            val opprettetJpMap = restTjenesteTestdata.hentResponseSomMap()
            journalpostIdPerKey[avvikData.testdataNokkel] = opprettetJpMap["journalpostId"] as String
        }
    }

    @Og("hendelse skal være publisert for opprettet data med nokkel {string}")
    fun `hendelse skal vare publisert for opprettet data`(nokkel: String) {
        val journalpostId = journalpostIdPerKey[nokkel]
        val response = restTjenesteTestdata.exchangeGet("/hendelser/$journalpostId")

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).containsSequence(journalpostId)
    }

    @Og("lag bidragssak {string} når den ikke finnes fra før:")
    fun `lag bidragssak nar den ikke finnes fra for`(saksnummer: String, bidragssak: String) {
        restTjenesteTestdata.exchangePost("/sak/$saksnummer", bidragssak)
    }
}
