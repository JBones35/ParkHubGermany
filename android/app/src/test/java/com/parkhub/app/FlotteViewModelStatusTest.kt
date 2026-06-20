package com.parkhub.app

import com.parkhub.app.data.BuchungDao
import com.parkhub.app.data.FahrerAusfallDao
import com.parkhub.app.data.FahrerDao
import com.parkhub.app.data.FahrerzuweisungDao
import com.parkhub.app.data.FahrzeugAusfallDao
import com.parkhub.app.data.FahrzeugDao
import com.parkhub.app.data.FahrzeugTypDao
import com.parkhub.app.model.Buchung
import com.parkhub.app.model.BuchungStatus
import com.parkhub.app.model.Fahrer
import com.parkhub.app.model.FahrerAusfall
import com.parkhub.app.model.FahrerStatus
import com.parkhub.app.model.Fahrerzuweisung
import com.parkhub.app.model.Fahrzeug
import com.parkhub.app.model.FahrzeugAusfall
import com.parkhub.app.model.FahrzeugStatus
import com.parkhub.app.model.FahrzeugTyp
import com.parkhub.app.ui.screens.FlotteViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class FlotteViewModelStatusTest {

    private val now = System.currentTimeMillis()
    private val vehicleType = FahrzeugTyp(
        id = UUID.fromString("10000000-0000-0000-0000-000000000001"),
        bezeichnung = "Test Transporter",
        breite_cm = 200f,
        laenge_cm = 500f,
        hoehe_cm = 220f,
        gewicht = "3,5 t"
    )
    private val parkingSpotId = UUID.fromString("20000000-0000-0000-0000-000000000001")
    private val logisticsId = UUID.fromString("30000000-0000-0000-0000-000000000001")

    @Test
    fun fahrzeugOhneBuchungOderAusfallIstFrei() = runBlocking {
        val vehicle = vehicle("KA-FR 100")
        val viewModel = viewModel(fahrzeuge = listOf(vehicle))

        val status = viewModel.fahrzeugMitStatusListe.first().single().status

        assertEquals(FahrzeugStatus.FREI, status)
    }

    @Test
    fun fahrzeugMitAktiverBuchungIstBesetzt() = runBlocking {
        val vehicle = vehicle("KA-BE 200")
        val booking = booking(vehicleId = vehicle.id, from = now - 1_000, until = now + 1_000)
        val viewModel = viewModel(fahrzeuge = listOf(vehicle), buchungen = listOf(booking))

        val status = viewModel.fahrzeugMitStatusListe.first().single().status

        assertEquals(FahrzeugStatus.BESETZT, status)
    }

    @Test
    fun fahrzeugMitAktuellemAusfallIstInWartung() = runBlocking {
        val vehicle = vehicle("KA-WA 300")
        val outage = FahrzeugAusfall(
            fahrzeugId = vehicle.id,
            von = now - 1_000,
            bis = now + 1_000,
            grund = "Werkstatt"
        )
        val viewModel = viewModel(fahrzeuge = listOf(vehicle), fahrzeugAusfaelle = listOf(outage))

        val status = viewModel.fahrzeugMitStatusListe.first().single().status

        assertEquals(FahrzeugStatus.WARTUNG, status)
    }

    @Test
    fun fahrzeugAusfallHatVorrangVorAktiverBuchung() = runBlocking {
        val vehicle = vehicle("KA-PR 400")
        val booking = booking(vehicleId = vehicle.id, from = now - 1_000, until = now + 1_000)
        val outage = FahrzeugAusfall(
            fahrzeugId = vehicle.id,
            von = now - 1_000,
            bis = now + 1_000,
            grund = "Werkstatt"
        )
        val viewModel = viewModel(
            fahrzeuge = listOf(vehicle),
            buchungen = listOf(booking),
            fahrzeugAusfaelle = listOf(outage)
        )

        val status = viewModel.fahrzeugMitStatusListe.first().single().status

        assertEquals(FahrzeugStatus.WARTUNG, status)
    }

    @Test
    fun fahrerOhneZuweisungOderAusfallIstFrei() = runBlocking {
        val driver = driver("Freier", "Fahrer")
        val viewModel = viewModel(fahrer = listOf(driver))

        val status = viewModel.fahrerMitStatusListe.first().single().status

        assertEquals(FahrerStatus.FREI, status)
    }

    @Test
    fun fahrerMitAktiverBuchungsZuweisungIstEingesetzt() = runBlocking {
        val driver = driver("Aktiver", "Fahrer")
        val vehicle = vehicle("KA-FA 500")
        val booking = booking(vehicleId = vehicle.id, from = now - 1_000, until = now + 1_000)
        val assignment = Fahrerzuweisung(
            buchungId = booking.id,
            fahrerId = driver.id,
            zugewiesenAm = now - 2_000
        )
        val viewModel = viewModel(
            fahrer = listOf(driver),
            fahrzeuge = listOf(vehicle),
            buchungen = listOf(booking),
            fahrerzuweisungen = listOf(assignment)
        )

        val status = viewModel.fahrerMitStatusListe.first().single().status

        assertEquals(FahrerStatus.EINGESETZT, status)
    }

    @Test
    fun fahrerAusfallHatVorrangVorAktiverZuweisung() = runBlocking {
        val driver = driver("Abwesender", "Fahrer")
        val vehicle = vehicle("KA-FA 600")
        val booking = booking(vehicleId = vehicle.id, from = now - 1_000, until = now + 1_000)
        val assignment = Fahrerzuweisung(
            buchungId = booking.id,
            fahrerId = driver.id,
            zugewiesenAm = now - 2_000
        )
        val absence = FahrerAusfall(
            fahrerId = driver.id,
            von = now - 1_000,
            bis = now + 1_000,
            grund = "Krank"
        )
        val viewModel = viewModel(
            fahrer = listOf(driver),
            fahrzeuge = listOf(vehicle),
            buchungen = listOf(booking),
            fahrerzuweisungen = listOf(assignment),
            fahrerAusfaelle = listOf(absence)
        )

        val status = viewModel.fahrerMitStatusListe.first().single().status

        assertEquals(FahrerStatus.ABWESEND, status)
    }

    private fun viewModel(
        fahrer: List<Fahrer> = emptyList(),
        fahrzeuge: List<Fahrzeug> = emptyList(),
        buchungen: List<Buchung> = emptyList(),
        fahrerzuweisungen: List<Fahrerzuweisung> = emptyList(),
        fahrerAusfaelle: List<FahrerAusfall> = emptyList(),
        fahrzeugAusfaelle: List<FahrzeugAusfall> = emptyList()
    ): FlotteViewModel =
        FlotteViewModel(
            fahrerDao = FakeFahrerDao(fahrer),
            fahrzeugDao = FakeFahrzeugDao(fahrzeuge),
            fahrzeugTypDao = FakeFahrzeugTypDao(listOf(vehicleType)),
            buchungDao = FakeBuchungDao(buchungen),
            fahrerzuweisungDao = FakeFahrerzuweisungDao(fahrerzuweisungen),
            fahrerAusfallDao = FakeFahrerAusfallDao(fahrerAusfaelle),
            fahrzeugAusfallDao = FakeFahrzeugAusfallDao(fahrzeugAusfaelle)
        )

    private fun vehicle(plate: String): Fahrzeug =
        Fahrzeug(
            kennzeichen = plate,
            fahrzeugTypId = vehicleType.id
        )

    private fun driver(firstName: String, lastName: String): Fahrer =
        Fahrer(
            vorname = firstName,
            nachname = lastName,
            lizenzNummer = "DE-${UUID.randomUUID()}"
        )

    private fun booking(vehicleId: UUID, from: Long, until: Long): Buchung =
        Buchung(
            stellplatzId = parkingSpotId,
            logistikId = logisticsId,
            fahrzeugId = vehicleId,
            von = from,
            bis = until,
            status = BuchungStatus.AKTIV
        )

    private class FakeFahrerDao(private val data: List<Fahrer>) : FahrerDao {
        override fun getAllFahrer(): Flow<List<Fahrer>> = flowOf(data)
        override suspend fun insertAll(liste: List<Fahrer>) = Unit
        override suspend fun delete(fahrer: Fahrer): Int = 1
    }

    private class FakeFahrzeugDao(private val data: List<Fahrzeug>) : FahrzeugDao {
        override fun getAllFahrzeug(): Flow<List<Fahrzeug>> = flowOf(data)
        override suspend fun insertAll(liste: List<Fahrzeug>) = Unit
        override suspend fun delete(fahrzeug: Fahrzeug): Int = 1
    }

    private class FakeFahrzeugTypDao(private val data: List<FahrzeugTyp>) : FahrzeugTypDao {
        override fun getAll(): Flow<List<FahrzeugTyp>> = flowOf(data)
        override suspend fun getById(id: UUID): FahrzeugTyp? = data.find { it.id == id }
        override suspend fun insertAll(typen: List<FahrzeugTyp>) = Unit
        override suspend fun delete(typ: FahrzeugTyp): Int = 1
    }

    private class FakeBuchungDao(private val data: List<Buchung>) : BuchungDao {
        override fun getAll(): Flow<List<Buchung>> = flowOf(data)
        override suspend fun insertAll(liste: List<Buchung>) = Unit
        override suspend fun delete(buchung: Buchung): Int = 1
    }

    private class FakeFahrerzuweisungDao(
        private val data: List<Fahrerzuweisung>
    ) : FahrerzuweisungDao {
        override fun getAll(): Flow<List<Fahrerzuweisung>> = flowOf(data)
        override suspend fun insertAll(liste: List<Fahrerzuweisung>) = Unit
        override suspend fun delete(zuweisung: Fahrerzuweisung): Int = 1
    }

    private class FakeFahrerAusfallDao(private val data: List<FahrerAusfall>) : FahrerAusfallDao {
        override fun getAll(): Flow<List<FahrerAusfall>> = flowOf(data)
        override suspend fun insertAll(liste: List<FahrerAusfall>) = Unit
        override suspend fun delete(ausfall: FahrerAusfall): Int = 1
    }

    private class FakeFahrzeugAusfallDao(
        private val data: List<FahrzeugAusfall>
    ) : FahrzeugAusfallDao {
        override fun getAll(): Flow<List<FahrzeugAusfall>> = flowOf(data)
        override suspend fun insertAll(liste: List<FahrzeugAusfall>) = Unit
        override suspend fun delete(ausfall: FahrzeugAusfall): Int = 1
    }
}
