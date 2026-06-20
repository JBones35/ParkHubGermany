package com.parkhub.app

import com.parkhub.app.model.FahrzeugStatus
import com.parkhub.app.model.fahrzeugListe
import com.parkhub.app.model.fahrzeugTypListe
import com.parkhub.app.ui.screens.FahrzeugMitStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FlotteScreenLogicTest {

    @Test
    fun fahrzeugListeEnthaeltAlleBeispielFahrzeuge() {
        assertEquals(6, fahrzeugListe.size)
    }

    @Test
    fun fahrzeugeWerdenMitPassendemFahrzeugTypKombiniert() {
        val fahrzeugeMitStatus = fahrzeugMitStatusListe()

        assertEquals(fahrzeugListe.size, fahrzeugeMitStatus.size)
        assertTrue(fahrzeugeMitStatus.all { it.typ?.id == it.fahrzeug.fahrzeugTypId })
    }

    @Test
    fun freiFilterGibtNurFreieFahrzeugeMitStatusZurueck() {
        val gefilterteListe = fahrzeugMitStatusListe()
            .filter { it.status == FahrzeugStatus.FREI }

        assertEquals(4, gefilterteListe.size)
        assertTrue(gefilterteListe.all { it.status == FahrzeugStatus.FREI })
    }

    @Test
    fun besetztFilterGibtNurBesetzteFahrzeugeMitStatusZurueck() {
        val gefilterteListe = fahrzeugMitStatusListe()
            .filter { it.status == FahrzeugStatus.BESETZT }

        assertEquals(1, gefilterteListe.size)
        assertEquals("KA-HE 4421", gefilterteListe.single().fahrzeug.kennzeichen)
        assertTrue(gefilterteListe.all { it.status == FahrzeugStatus.BESETZT })
    }

    @Test
    fun wartungFilterGibtNurFahrzeugeMitStatusInWartungZurueck() {
        val gefilterteListe = fahrzeugMitStatusListe()
            .filter { it.status == FahrzeugStatus.WARTUNG }

        assertEquals(1, gefilterteListe.size)
        assertEquals("KA-DH 8801", gefilterteListe.single().fahrzeug.kennzeichen)
        assertTrue(gefilterteListe.all { it.status == FahrzeugStatus.WARTUNG })
    }

    private fun fahrzeugMitStatusListe(): List<FahrzeugMitStatus> =
        fahrzeugListe.mapIndexed { index, fahrzeug ->
            val status = when (index) {
                1 -> FahrzeugStatus.BESETZT
                2 -> FahrzeugStatus.WARTUNG
                else -> FahrzeugStatus.FREI
            }

            FahrzeugMitStatus(
                fahrzeug = fahrzeug,
                typ = fahrzeugTypListe.find { it.id == fahrzeug.fahrzeugTypId },
                status = status
            )
        }
}
