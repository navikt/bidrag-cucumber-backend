package no.nav.bidrag.cucumber

import io.cucumber.core.api.Scenario

open class BidragCucumberScenarioManager {
    companion object {
        const val ADD_LINEFEED = "&add.linefeed"
        private val scenarioMessages = HashSet<ScenarioMessage>()
        private var scenario: Scenario? = null
        var scenarioName: String? = null

        var correlationIdForScenario: String? = null

        fun use(scenario: Scenario) {
            this.scenario = scenario
            correlationIdForScenario = Environment.createCorrelationIdValue()
            scenarioMessages.clear()
            scenarioName = scenario.name
        }

        fun writeOnceToCucumberScenario(scenarioMessage: ScenarioMessage, message: String) {
            if (!scenarioMessages.contains(scenarioMessage)) {
                writeToCucumberScenario(message)
                scenarioMessages.add(scenarioMessage)
            }
        }

        fun writeToCucumberScenario(message: String) {
            if (scenario != null) {
                scenario!!.write("<p>\n${message.replace(ADD_LINEFEED, "\n<br>")}\n</p>")
            }
        }
    }
}

enum class ScenarioMessage { CORELATION_ID }
