package no.nav.bidrag.cucumber

import io.cucumber.java.Scenario
import java.time.LocalDate

open class ScenarioManager {
    companion object {
        private var scenario: Scenario? = null
        private var correlationIdForScenario: String? = null

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

        fun createQueryLinkForCorrelationId(): String {
            val now = LocalDate.now()
            val year = now.year
            val month = now.monthValue
            val dayOfMonth = now.dayOfMonth

            val time = "time:(from:'${"$year-$month-$dayOfMonth"}T00:00:00.000Z',to:'${"$year-$month-$dayOfMonth"}T23:59:59.999Z')"
            val columns = "columns:!(message,level,application)"
            val index = "index:'96e648c0-980a-11e9-830a-e17bbd64b4db'"
            val query = "query:(language:lucene,query:\"${correlationIdForScenario}\")"
            val sort = "sort:!(!('@timestamp',desc))"

            return "https://logs.adeo.no/app/kibana#/discover?_g=($time&_a=($columns,$index,interval:auto,$query,$sort)"
        }

        fun createCorrelationIdLinkTitle() = "Link for correlation-id (${correlationIdForScenario}):"
        fun fetchCorrelationIdForScenario() = correlationIdForScenario
    }
}
