package no.nav.bidrag.cucumber

import io.cucumber.java.Scenario

open class ScenarioManager {
    companion object {
        private var scenario: Scenario? = null

        var correlationIdForScenario: String? = null

        fun use(scenario: Scenario) {
            this.scenario = scenario
            correlationIdForScenario = Environment.createCorrelationIdValue()
        }

        fun writeToCucumberScenario(message: String) {
            writeToCucumberScenario(null, message)
        }

        fun writeToCucumberScenario(messageTitle: String?, message: String) {
            if (scenario != null) {
                if (messageTitle != null) {
                    scenario!!.log("<h5>\n$messageTitle\n</h5>")
                }

                scenario!!.log("<p>\n$message\n</p>")
            } else {
                System.err.println("cannot write '$message' to scenario")
            }
        }
    }
}
