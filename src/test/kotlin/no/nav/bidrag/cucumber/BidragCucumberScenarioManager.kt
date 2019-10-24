package no.nav.bidrag.cucumber

import io.cucumber.core.api.Scenario

open class BidragCucumberScenarioManager {
    companion object {
        private var scenario: Scenario? = null

        var correlationIdForScenario: String? = null

        fun use(scenario: Scenario) {
            this.scenario = scenario
            correlationIdForScenario = Environment.createCorrelationIdValue()
            writeToCucumberScenario(
                    "Link til kibana for correlation-id: $correlationIdForScenario\n\n" +
                            "https://logs.adeo.no/app/kibana#/discover?_g=()&_a=(columns:!(message,envclass,environment,level,application,host),index:'96e648c0-980a-11e9-830a-e17bbd64b4db',interval:auto,query:(language:lucene,query:\"$correlationIdForScenario\"),sort:!('@timestamp',desc))\n"
            )
        }

        fun writeToCucumberScenario(message: String) {
            if (scenario != null) {
                scenario!!.write(message)
            }
        }
    }
}
