package com.parkhub.app

import com.parkhub.app.model.stellplatzListe
import com.parkhub.app.ui.components.suche.alsEntfernungText
import com.parkhub.app.ui.components.suche.aufMitternachtSetzen
import com.parkhub.app.ui.components.suche.sortierOptionen
import com.parkhub.app.ui.screens.StellplatzFilter
import com.parkhub.app.ui.screens.StellplatzMitDetails
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

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
    fun stellplatzFilterHatErwarteteStandardwerte() {
        val filter = StellplatzFilter()

        assertEquals(300f, filter.minLaenge, 0.0f)
        assertEquals(180f, filter.minBreite, 0.0f)
        assertEquals(180f, filter.minHoehe, 0.0f)
        assertEquals(2.0f, filter.minPreis, 0.0f)
        assertEquals(8.0f, filter.maxPreis, 0.0f)
        assertEquals(0f, filter.minBewertung, 0.0f)
        assertTrue(filter.preisAufsteigend)
    }

    @Test
    fun stellplatzFilterKannPreisSortierungAufAbsteigendSetzen() {
        val filter = StellplatzFilter().copy(preisAufsteigend = false)

        assertEquals(false, filter.preisAufsteigend)
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

    @Test
    fun aufMitternachtSetzenEntferntUhrzeitVomZeitpunkt() {
        val zeitpunkt = Calendar.getInstance().apply {
            set(2026, Calendar.JUNE, 20, 15, 45, 12)
            set(Calendar.MILLISECOND, 345)
        }.timeInMillis

        val mitternacht = Calendar.getInstance().apply {
            timeInMillis = aufMitternachtSetzen(zeitpunkt)
        }

        assertEquals(2026, mitternacht.get(Calendar.YEAR))
        assertEquals(Calendar.JUNE, mitternacht.get(Calendar.MONTH))
        assertEquals(20, mitternacht.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, mitternacht.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, mitternacht.get(Calendar.MINUTE))
        assertEquals(0, mitternacht.get(Calendar.SECOND))
        assertEquals(0, mitternacht.get(Calendar.MILLISECOND))
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
