package no.nav.bidrag.cucumber

import io.cucumber.core.api.Scenario

open class BidragCucumberScenarioManager {
    companion object {
        private val scenarioMessages = HashSet<ScenarioMessage>()
        private var scenario: Scenario? = null

        var correlationIdForScenario: String? = null

        fun use(scenario: Scenario) {
            this.scenario = scenario
            correlationIdForScenario = Environment.createCorrelationIdValue()
            scenarioMessages.clear()
        }

        fun writeOnceToCucumberScenario(scenarioMessage: ScenarioMessage, messageTitle: String?, message: String) {
            if (!scenarioMessages.contains(scenarioMessage)) {
                writeToCucumberScenario(messageTitle, message)
                scenarioMessages.add(scenarioMessage)
            }
        }

        fun writeToCucumberScenario(message: String) {
            writeToCucumberScenario(null, message)
        }

        fun writeToCucumberScenario(messageTitle: String?, message: String) {
            if (scenario != null) {
                writeScenarioTitle()

                if (messageTitle != null) {
                    scenario!!.write("<h5>\n$messageTitle\n</h5>")
                }

                scenario!!.write("<p>\n$message\n</p>")
            }
        }

        private fun writeScenarioTitle() {
            if (!scenarioMessages.contains(ScenarioMessage.SCENARIO_TITLE)) {
                scenario!!.write("<h4>\n${scenario!!.name}\n</h4>")
                scenarioMessages.add(ScenarioMessage.SCENARIO_TITLE)
            }
        }
    }
}

enum class ScenarioMessage { CORRELATION_ID, SCENARIO_TITLE }
