package no.nav.bidrag.cucumber

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import no.nav.bidrag.cucumber.dokument.RestTjenesteTestdata
import org.junit.AfterClass
import org.junit.runner.RunWith

/**
 * Runner class which looks for feature-files (in src/test/resources/no.nav.bidrag.cucumber.*) to test
 */
@RunWith(Cucumber::class)
@CucumberOptions(plugin = ["pretty", "json:target/cucumber-report/cucumber.json"])
class RunCucumberTest {
    companion object {
        @AfterClass
        @JvmStatic
        fun deleteCreatedTestdata() {
            val restTjenesteTestdata = RestTjenesteTestdata("bidragDokumentTestdata")
            restTjenesteTestdata.slettOpprettedeData()
        }
    }
}
