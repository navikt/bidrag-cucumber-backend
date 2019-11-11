package no.nav.bidrag.cucumber.sak

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Når
import no.nav.bidrag.cucumber.BidragCucumberScenarioManager
import no.nav.bidrag.cucumber.FellesEgenskaper

class SakEgenskap {

    @Before
    fun `manage scenario`(scenario: Scenario) {
        BidragCucumberScenarioManager.use(scenario)
    }

    @Når("jeg henter bidragssaker for person med fnr {string}")
    fun jeg_henter_bidragssaker_for_person_med_fnr(fnr: String) {
        FellesEgenskaper.restTjeneste.exchangeGet("/person/sak/$fnr")
    }
}