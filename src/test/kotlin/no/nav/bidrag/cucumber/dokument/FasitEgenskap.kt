package no.nav.bidrag.cucumber.dokument

import io.cucumber.java.no.Når
import io.cucumber.java.no.Så
import no.nav.bidrag.cucumber.RestTjenesteEgenskap
import org.assertj.core.api.Assertions.assertThat


class FasitEgenskap {

    private var restTjenesteEgenskap = RestTjenesteEgenskap()

    @Når("det gjøres et kall til {string}")
    fun `det gjores et kall til`(endpointUrl: String) {
        restTjenesteEgenskap.get(endpointUrl)
    }

    @Så("skal responsen inneholde json med property {string} og verdi {string}")
    fun `skal responsen inneholde json med property og verdi`(property: String, verdi: String) {
        assertThat(restTjenesteEgenskap.response()).contains(""""$property":"$verdi"""")
    }

    @Så("skal aktuell klasse være {string}")
    fun `skal aktuell klasse vaere`(forventetEgenskap: String) {
        assertThat(this.javaClass.simpleName).isEqualTo(forventetEgenskap)
    }

    @Så("skal html responsen være {string}")
    fun `skal html responsen vaere`(httmlStatus: String) {
        // Write code here that turns the phrase above into concrete actions
        throw cucumber.api.PendingException()
    }
}
