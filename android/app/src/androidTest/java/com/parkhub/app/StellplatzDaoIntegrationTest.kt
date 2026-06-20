package com.parkhub.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.parkhub.app.data.AppDatabase
import com.parkhub.app.model.Adresse
import com.parkhub.app.model.Sperrzeit
import com.parkhub.app.model.Stellplatz
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class StellplatzDaoIntegrationTest {

    private lateinit var db: AppDatabase

    private val searchStart = 1_000L
    private val searchEnd = 2_000L

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun getGefiltertSortiertPreisAufsteigend() = runBlocking {
        val expensive = stellplatz("Teuer", preis = 6.0f)
        val cheap = stellplatz("Guentig", preis = 3.0f)
        seedStellplaetze(expensive, cheap)

        val result = query(preisAufsteigend = true)

        assertEquals(listOf("Guentig", "Teuer"), result.map { it.vermieter })
    }

    @Test
    fun getGefiltertFiltertZuKleineStellplaetzeAus() = runBlocking {
        val tooShort = stellplatz("Zu kurz", laenge = 450f)
        val matching = stellplatz("Passend", laenge = 650f)
        seedStellplaetze(tooShort, matching)

        val result = query(minLaenge = 600f)

        assertEquals(listOf("Passend"), result.map { it.vermieter })
    }

    @Test
    fun getGefiltertSchliesstStellplatzMitUeberlappenderSperrzeitAus() = runBlocking {
        val blocked = stellplatz("Blockiert")
        val available = stellplatz("Verfuegbar")
        seedStellplaetze(blocked, available)
        db.sperrzeitDao().insertAll(
            listOf(
                Sperrzeit(
                    stellplatzId = blocked.id,
                    von = searchStart - 100,
                    bis = searchStart + 100,
                    grund = "Wartung"
                )
            )
        )

        val result = query()

        assertEquals(listOf("Verfuegbar"), result.map { it.vermieter })
    }

    private suspend fun seedStellplaetze(vararg stellplaetze: Stellplatz) {
        db.adresseDao().insertAll(stellplaetze.map { adresse(it.adresseId) })
        db.stellplatzDao().insertAll(stellplaetze.toList())
    }

    private suspend fun query(
        minLaenge: Float = 0f,
        minBreite: Float = 0f,
        minHoehe: Float = 0f,
        minPreis: Float = 0f,
        maxPreis: Float = 100f,
        preisAufsteigend: Boolean = true
    ): List<Stellplatz> =
        db.stellplatzDao().getGefiltert(
            minFahrzeugLaenge = minLaenge,
            minFahrzeugBreite = minBreite,
            minFahrzeugHoehe = minHoehe,
            minPreis = minPreis,
            maxPreis = maxPreis,
            minBewertung = 0f,
            von = searchStart,
            bis = searchEnd,
            preisAufsteigend = preisAufsteigend
        ).first()

    private fun stellplatz(
        vermieter: String,
        laenge: Float = 600f,
        breite: Float = 250f,
        hoehe: Float = 220f,
        preis: Float = 4.0f
    ): Stellplatz =
        Stellplatz(
            vermieter = vermieter,
            adresseId = UUID.randomUUID(),
            breite_cm = breite,
            laenge_cm = laenge,
            hoehe_cm = hoehe,
            preis_stunde = preis,
            gps_lat = 49.0f,
            gps_lng = 8.0f
        )

    private fun adresse(id: UUID): Adresse =
        Adresse(
            id = id,
            strasse = "Teststrasse",
            hausnummer = "1",
            plz = "76131",
            ort = "Karlsruhe"
        )
}
