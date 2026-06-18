package com.parkhub.app

import com.parkhub.app.model.stellplatzListe
import com.parkhub.app.ui.components.suche.alsEntfernungText
import com.parkhub.app.ui.screens.StellplatzMitDetails
import com.parkhub.app.ui.screens.sortierOptionen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SucheScreenLogicTest {

    @Test
    fun sortierOptionenSindInErwarteterReihenfolgeVorhanden() {
        val labels = sortierOptionen.map { it.label }

        assertEquals(
            listOf(
                "Preis aufsteigend",
                "Preis absteigend",
                "Entfernung aufsteigend",
                "Entfernung absteigend"
            ),
            labels
        )
    }

    @Test
    fun preisAufsteigendSortiertGuentigstenStellplatzNachVorne() {
        val sortierteListe = stellplatzListe.sortedBy { it.preis_stunde }

        assertEquals("Sabine Klein", sortierteListe.first().vermieter)
        assertEquals(2.80f, sortierteListe.first().preis_stunde, 0.0f)
    }

    @Test
    fun preisAbsteigendSortiertTeuerstenStellplatzNachVorne() {
        val sortierteListe = stellplatzListe.sortedByDescending { it.preis_stunde }

        assertEquals(5.20f, sortierteListe.first().preis_stunde, 0.0f)
    }

    @Test
    fun entfernungAufsteigendSortiertNaechstenStellplatzNachVorne() {
        val stellplaetzeMitEntfernung = listOf(
            stellplatzMitEntfernung(index = 0, entfernungMeter = 900),
            stellplatzMitEntfernung(index = 1, entfernungMeter = 150),
            stellplatzMitEntfernung(index = 2, entfernungMeter = 500)
        )

        val sortierteListe = stellplaetzeMitEntfernung.sortedBy { it.entfernungMeter }

        assertEquals(150, sortierteListe.first().entfernungMeter)
        assertEquals(stellplatzListe[1].id, sortierteListe.first().stellplatz.id)
    }

    @Test
    fun entfernungAbsteigendSortiertEntferntestenStellplatzNachVorne() {
        val stellplaetzeMitEntfernung = listOf(
            stellplatzMitEntfernung(index = 0, entfernungMeter = 900),
            stellplatzMitEntfernung(index = 1, entfernungMeter = 150),
            stellplatzMitEntfernung(index = 2, entfernungMeter = 500)
        )

        val sortierteListe = stellplaetzeMitEntfernung.sortedByDescending { it.entfernungMeter }

        assertEquals(900, sortierteListe.first().entfernungMeter)
        assertEquals(stellplatzListe[0].id, sortierteListe.first().stellplatz.id)
    }

    @Test
    fun entfernungTextZeigtMeterUnterEinemKilometer() {
        assertEquals("999 m", 999.alsEntfernungText())
    }

    @Test
    fun entfernungTextZeigtKilometerAbEinemKilometer() {
        val entfernungText = 1100.alsEntfernungText()

        assertTrue(entfernungText.endsWith(" km"))
        assertTrue(entfernungText.startsWith("1"))
    }

    private fun stellplatzMitEntfernung(index: Int, entfernungMeter: Int): StellplatzMitDetails =
        StellplatzMitDetails(
            stellplatz = stellplatzListe[index],
            adresse = null,
            bewertungSchnitt = 0f,
            anzahlBewertungen = 0,
            entfernungMeter = entfernungMeter
        )
}
