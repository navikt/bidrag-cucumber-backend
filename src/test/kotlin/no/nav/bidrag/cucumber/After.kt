package no.nav.bidrag.cucumber

import io.cucumber.java.After
import io.cucumber.junit.Cucumber
import no.nav.bidrag.cucumber.dokument.RestTjenesteTestdata
import org.junit.runner.RunWith

/**
 * Clean up database testdata after all of certain tags are tested
 */
@RunWith(Cucumber::class)
class After {
    companion object {
        @After("@bidrag-dokument,@bidrag-dokument-journalpost")
        @JvmStatic
        fun deleteCreatedTestdata() {
            val restTjenesteTestdata = RestTjenesteTestdata("bidragDokumentTestdata")
            restTjenesteTestdata.slettOpprettedeData()
        }
    }
}
