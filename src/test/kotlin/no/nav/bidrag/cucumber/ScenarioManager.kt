package no.nav.bidrag.cucumber

import io.cucumber.java.Scenario
import org.slf4j.LoggerFactory
import java.time.LocalDate

open class ScenarioManager {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ScenarioManager::class.java)
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
                LOGGER.error("cannot log '$message' to scenario")
            }
        }

        fun createQueryLinkForCorrelationId(): String {
            val now = LocalDate.now()
            val year = now.year
            val month = if (now.monthValue < 10) "0${now.monthValue}" else "${now.monthValue}"
            val dayOfMonth = if (now.monthValue > 9) "${now.dayOfMonth}" else "0${now.dayOfMonth}"

            val time = "time:(from:%27${"$year-$month-$dayOfMonth"}T00:00:00.000Z%27,to:%27${"$year-$month-$dayOfMonth"}T23:59:59.999<Z%27)"
            val columns = "columns:!(message,level,application)"
            val index = "index:%2796e648c0-980a-11e9-830a-e17bbd64b4db%27"
            val query = "query:(language:lucene,query:%22$correlationIdForScenario%22)"
            val sort = "sort:!(!(%27@timestamp%27,desc))"

            return "https://logs.adeo.no/app/kibana#/discover?_g=($time)&_a=($columns,$index,interval:auto,$query,$sort)"
        }

        fun createCorrelationIdLinkTitle() = "Link for correlation-id ($correlationIdForScenario):"
        fun fetchCorrelationIdForScenario() = correlationIdForScenario
    }
}
