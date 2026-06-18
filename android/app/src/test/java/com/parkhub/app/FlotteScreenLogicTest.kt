package com.parkhub.app

import com.parkhub.app.model.FahrzeugStatus
import com.parkhub.app.model.fahrzeugListe
import com.parkhub.app.model.fahrzeugTypListe
import com.parkhub.app.ui.screens.FahrzeugMitTyp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FlotteScreenLogicTest {

    @Test
    fun fahrzeugListeEnthaeltAlleBeispielFahrzeuge() {
        assertEquals(4, fahrzeugListe.size)
    }

    @Test
    fun fahrzeugeWerdenMitPassendemFahrzeugTypKombiniert() {
        val fahrzeugeMitTyp = fahrzeugMitTypListe()

        assertEquals(fahrzeugListe.size, fahrzeugeMitTyp.size)
        assertTrue(fahrzeugeMitTyp.all { it.typ?.id == it.fahrzeug.fahrzeugTypId })
    }

    @Test
    fun aktivFilterGibtNurAktiveFahrzeugeMitTypZurueck() {
        val gefilterteListe = fahrzeugMitTypListe()
            .filter { it.fahrzeug.status == FahrzeugStatus.AKTIV }

        assertEquals(3, gefilterteListe.size)
        assertTrue(gefilterteListe.all { it.fahrzeug.status == FahrzeugStatus.AKTIV })
    }

    @Test
    fun wartungFilterGibtNurFahrzeugeMitTypInWartungZurueck() {
        val gefilterteListe = fahrzeugMitTypListe()
            .filter { it.fahrzeug.status == FahrzeugStatus.WARTUNG }

        assertEquals(1, gefilterteListe.size)
        assertEquals("KA-DH 8801", gefilterteListe.single().fahrzeug.kennzeichen)
        assertTrue(gefilterteListe.all { it.fahrzeug.status == FahrzeugStatus.WARTUNG })
    }

    private fun fahrzeugMitTypListe(): List<FahrzeugMitTyp> =
        fahrzeugListe.map { fahrzeug ->
            FahrzeugMitTyp(
                fahrzeug = fahrzeug,
                typ = fahrzeugTypListe.find { it.id == fahrzeug.fahrzeugTypId }
            )
        }
}
