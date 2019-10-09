package no.nav.bidrag.cucumber

import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Så
import org.assertj.core.api.Assertions.assertThat
import java.io.File

class BidragCucumberEgenskap {

    private lateinit var fixturesFraKildekode: FixturesFraKildekode

    @Gitt("filsti til kildekode: {string}")
    fun `filsti for kildekode med stegdefinisjoner`(kildesti: String) {
        fixturesFraKildekode = FixturesFraKildekode(kildesti)
    }

    @Når("man sjekker for duplikater")
    fun `man sjekker for duplikater`() {
        fixturesFraKildekode.finnDuplikater()
    }

    @Så("skal det ikke finnes duplikater")
    fun `skal det ikke finnes duplikater`() {
        val duplikater = fixturesFraKildekode.hentDuplikater()

        assertThat(duplikater).withFailMessage("Duplikater: %s", duplikater.joinToString("\n\t\t", "\n\t\t")).isEmpty()
    }

    @Gitt("fixture-annotasjon blir lagt til: {string}")
    fun `folgende fixture annotasjon blir lagt til`(fixtureAnnotasjon: String) {
        fixturesFraKildekode.leggTilDuplikat(fixtureAnnotasjon, this.javaClass.name)
    }

    @Så("skal det finnes duplikater")
    fun `skal det finnes duplikater`() {
        val duplikater = fixturesFraKildekode.hentDuplikater()

        assertThat(duplikater).withFailMessage("ingen duplikater").isNotEmpty
    }
}

private const val FIXTURE_GITT = "@Gitt"
private const val FIXTURE_NAAR = "@Når"
private const val FIXTURE_SAA = "@Så"

internal class FixturesFraKildekode(
        private val sourceFolder: File
) {
    private var stegdefinisjonerFraKilde: MutableList<File> = ArrayList()
    private var gherkinFixtures: MutableMap<String, MutableList<GherkinFixture>> = HashMap()

    constructor(sourcePath: String) : this(File(sourcePath))

    internal fun finnDuplikater() {
        les(sourceFolder)
        assertThat(stegdefinisjonerFraKilde).withFailMessage("stegdefinisjoner fra kildemappe").isNotEmpty
        stegdefinisjonerFraKilde.forEach { finnDupliserteAnnotasjoner(it) }
    }

    private fun les(mappe: File) {
        val mappensFiler = mappe.listFiles()

        for (kildefil in mappensFiler!!) {
            if (kildefil.isDirectory) {
                les(kildefil)
            } else if (kildefil.name.endsWith(".kt")) {
                stegdefinisjonerFraKilde.add(kildefil)
            }
        }
    }

    private fun finnDupliserteAnnotasjoner(stegdefinisjon: File) {
        stegdefinisjon.forEachLine { taVarePaGherkinFixture(it, stegdefinisjon.absolutePath) }
    }

    private fun taVarePaGherkinFixture(linje: String, fraKildefil: String) {
        val linjeTrimmed = linje.trim()

        if (linjeTrimmed.startsWith(FIXTURE_GITT) || linjeTrimmed.startsWith(FIXTURE_NAAR) || linjeTrimmed.startsWith(FIXTURE_SAA)) {
            gherkinFixtures.getOrPut(linjeTrimmed) { mutableListOf() }.add(GherkinFixture(linjeTrimmed, fraKildefil))
        }
    }

    internal fun hentDuplikater(): List<GherkinFixture> {
        val duplikater = mutableListOf<GherkinFixture>()

        gherkinFixtures.forEach { _, listeAvTilfeller -> leggTilDuplikater(listeAvTilfeller, duplikater) }

        return duplikater
    }

    private fun leggTilDuplikater(listeAvTilfeller: MutableList<GherkinFixture>, duplikater: MutableList<GherkinFixture>) {
        if (listeAvTilfeller.size > 1) {
            duplikater.addAll(listeAvTilfeller)
        }
    }

    internal fun leggTilDuplikat(fixtureAnnotasjon: String, simpleName: String?) {
        gherkinFixtures.getOrPut(fixtureAnnotasjon) { mutableListOf() }
                .add(GherkinFixture(fixtureAnnotasjon, simpleName ?: "FixturesFraKildekode.kt"))
    }

    internal data class GherkinFixture(
            private val annotationText: String,
            private var kildeSti: String
    )
}
