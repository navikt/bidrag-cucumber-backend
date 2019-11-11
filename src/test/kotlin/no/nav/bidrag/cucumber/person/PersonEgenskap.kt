package no.nav.bidrag.cucumber.person

import io.cucumber.core.api.Scenario
import io.cucumber.java.Before
import io.cucumber.java.no.Når
import no.nav.bidrag.cucumber.ScenarioManager
import no.nav.bidrag.cucumber.FellesEgenskaper.Companion.restTjeneste

class PersonEgenskap {

    @Before
    fun `manage scenario`(scenario: Scenario) {
        ScenarioManager.use(scenario)
    }

    @Når("jeg henter informasjon for ident {string}")
    fun `jeg henter informasjon for ident`(ident: String) {
        restTjeneste.exchangeGet("/informasjon/$ident")
    }
}