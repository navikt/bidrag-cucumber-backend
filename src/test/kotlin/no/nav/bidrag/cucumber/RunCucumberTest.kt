package no.nav.bidrag.cucumber

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import no.nav.bidrag.cucumber.dokument.RestTjenesteTestdata
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory

/**
 * Runner class which looks for feature-files (in src/test/resources/no.nav.bidrag.cucumber.*) to test
 */
@RunWith(Cucumber::class)
@CucumberOptions(plugin = ["pretty", "json:target/cucumber-report/cucumber.json"])
class RunCucumberTest {
    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(RunCucumberTest::class.java)

        private lateinit var response: Map<String, Any>
        private val restTjenesteTestdata = RestTjenesteTestdata("bidragDokumentTestdata")

        @BeforeClass
        @JvmStatic
        fun fetchMaxDatabaseIds() {
            restTjenesteTestdata.exchangeGet("journal/max/ids")
            response = restTjenesteTestdata.hentResponseSomMap()
            LOGGER.info(">>>> $response <<<<")
        }

        @AfterClass
        @JvmStatic
        fun deleteCreatedTestdata() {
            val fraJpId = response["maxJournalpostId"]
            val fraJsakId = response["maxJournalsakId"]
            val endpointUrl = "journal/delete/fra-jp-id/$fraJpId/fra-jsak-id/$fraJsakId"
            ScenarioManager.useScenarioForLogging = false
            restTjenesteTestdata.exchangeDelete(endpointUrl)
            LOGGER.info("slettet testdata (ikke mottaksregistrert: $endpointUrl")
        }
    }
}