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

        fun log(message: String) {
            log(null, message)
        }

        fun log(messageTitle: String?, message: String) {
            if (scenario != null) {
                val title = if (messageTitle != null) "<h5>$messageTitle</h5>" else ""
                scenario!!.log("$title<p>\n$message\n</p>")
            } else {
                System.err.println("cannot log '$message' to scenario")
            }
        }
    }
}
