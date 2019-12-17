package no.nav.bidrag.cucumber

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import no.nav.bidrag.cucumber.dokument.ApplikasjonEgenskap
import org.junit.runner.RunWith

/**
 * Runner class which looks for feature-files (in src/test/resources/no.nav.bidrag.cucumber.*) to test
 */
@RunWith(Cucumber::class)
@CucumberOptions(plugin = ["pretty", "json:target/cucumber-report/cucumber.json"])
class RunCucumberTest {

    @Before
    fun `manage scenario`(scenario: Scenario) {
        ScenarioManager.use(scenario)
    }
}
