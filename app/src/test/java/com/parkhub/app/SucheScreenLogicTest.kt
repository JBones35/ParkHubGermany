package com.parkhub.app

import com.parkhub.app.ui.screens.StellplatzVorschau
import com.parkhub.app.ui.screens.sortierOptionen
import com.parkhub.app.ui.screens.stellplatzVorschauListe
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
        val sortierteListe = stellplatzVorschauListe.sortedBy { it.preisProStunde }

        assertEquals(3, sortierteListe.first().id)
        assertEquals(2.80, sortierteListe.first().preisProStunde, 0.0)
    }

    @Test
    fun preisAbsteigendSortiertTeuerstenStellplatzNachVorne() {
        val sortierteListe = stellplatzVorschauListe.sortedByDescending { it.preisProStunde }

        assertEquals(5, sortierteListe.first().id)
        assertEquals(5.20, sortierteListe.first().preisProStunde, 0.0)
    }

    @Test
    fun entfernungAufsteigendSortiertNaechstenStellplatzNachVorne() {
        val sortierteListe = stellplatzVorschauListe.sortedBy { it.entfernung }

        assertEquals(1, sortierteListe.first().id)
        assertEquals(350, sortierteListe.first().entfernung)
    }

    @Test
    fun entfernungAbsteigendSortiertEntferntestenStellplatzNachVorne() {
        val sortierteListe = stellplatzVorschauListe.sortedByDescending { it.entfernung }

        assertEquals(5, sortierteListe.first().id)
        assertEquals(1400, sortierteListe.first().entfernung)
    }

    @Test
    fun entfernungTextZeigtMeterUnterEinemKilometer() {
        val stellplatz = StellplatzVorschau(
            id = 10,
            name = "Testplatz",
            entfernung = 999,
            preisProStunde = 1.0,
            bewertung = 4.0f,
            anzahlBewertungen = 1
        )

        assertEquals("999 m", stellplatz.entfernungText)
    }

    @Test
    fun entfernungTextZeigtKilometerAbEinemKilometer() {
        val stellplatz = StellplatzVorschau(
            id = 11,
            name = "Testplatz",
            entfernung = 1100,
            preisProStunde = 1.0,
            bewertung = 4.0f,
            anzahlBewertungen = 1
        )

        assertTrue(stellplatz.entfernungText.endsWith(" km"))
        assertTrue(stellplatz.entfernungText.startsWith("1"))
    }
}
