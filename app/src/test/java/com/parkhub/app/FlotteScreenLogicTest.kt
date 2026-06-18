package com.parkhub.app

import com.parkhub.app.model.FahrzeugStatus
import com.parkhub.app.model.fahrzeugListe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FlotteScreenLogicTest {

    @Test
    fun fahrzeugListeEnthaeltAlleBeispielFahrzeuge() {
        assertEquals(4, fahrzeugListe.size)
    }

    @Test
    fun aktivFilterGibtNurAktiveFahrzeugeZurueck() {
        val gefilterteListe = fahrzeugListe.filter { it.status == FahrzeugStatus.AKTIV }

        assertEquals(3, gefilterteListe.size)
        assertTrue(gefilterteListe.all { it.status == FahrzeugStatus.AKTIV })
    }

    @Test
    fun wartungFilterGibtNurFahrzeugeInWartungZurueck() {
        val gefilterteListe = fahrzeugListe.filter { it.status == FahrzeugStatus.WARTUNG }

        assertEquals(1, gefilterteListe.size)
        assertEquals("KA-DH 8801", gefilterteListe.single().kennzeichen)
        assertTrue(gefilterteListe.all { it.status == FahrzeugStatus.WARTUNG })
    }
}
