package com.parkhub.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parkhub.app.data.*
import com.parkhub.app.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class FahrerMitStatus(
    val fahrer: Fahrer,
    val status: FahrerStatus
)

data class FahrzeugMitStatus(
    val fahrzeug: Fahrzeug,
    val typ: FahrzeugTyp?,
    val status: FahrzeugStatus
)

class FlotteViewModel(
    private val fahrerDao: FahrerDao,
    private val fahrzeugDao: FahrzeugDao,
    private val fahrzeugTypDao: FahrzeugTypDao,
    private val buchungDao: BuchungDao,
    private val fahrerzuweisungDao: FahrerzuweisungDao,
    private val fahrerAusfallDao: FahrerAusfallDao,
    private val fahrzeugAusfallDao: FahrzeugAusfallDao
) : ViewModel() {

    // Fahrer-Status: ABWESEND wenn aktueller Ausfall läuft,
    // sonst EINGESETZT wenn aktuell eine Buchung über Fahrerzuweisung läuft,
    // sonst FREI.
    val fahrerMitStatusListe: Flow<List<FahrerMitStatus>> = combine(
        fahrerDao.getAllFahrer(),
        fahrerzuweisungDao.getAll(),
        buchungDao.getAll(),
        fahrerAusfallDao.getAll()
    ) { fahrerListe, zuweisungen, buchungen, ausfaelle ->
        val jetzt = System.currentTimeMillis()

        fahrerListe.map { fahrer ->
            val hatAusfall = ausfaelle.any { ausfall ->
                ausfall.fahrerId == fahrer.id && ausfall.von <= jetzt && ausfall.bis >= jetzt
            }

            val status = if (hatAusfall) {
                FahrerStatus.ABWESEND
            } else {
                val istEingesetzt = zuweisungen
                    .filter { it.fahrerId == fahrer.id }
                    .any { zuweisung ->
                        val buchung = buchungen.find { it.id == zuweisung.buchungId }
                        buchung != null &&
                                buchung.status == BuchungStatus.AKTIV &&
                                buchung.von <= jetzt && buchung.bis >= jetzt
                    }
                if (istEingesetzt) FahrerStatus.EINGESETZT else FahrerStatus.FREI
            }

            FahrerMitStatus(fahrer, status)
        }
    }

    // Fahrzeug-Status: WARTUNG wenn aktueller Ausfall läuft,
    // sonst BESETZT wenn aktuell eine aktive Buchung läuft,
    // sonst AKTIV.
    val fahrzeugMitStatusListe: Flow<List<FahrzeugMitStatus>> = combine(
        fahrzeugDao.getAllFahrzeug(),
        fahrzeugTypDao.getAll(),
        fahrzeugAusfallDao.getAll(),
        buchungDao.getAll()
    ) { fahrzeugListe, typen, ausfaelle, buchungen ->
        val jetzt = System.currentTimeMillis()

        fahrzeugListe.map { fahrzeug ->
            val hatAusfall = ausfaelle.any { ausfall ->
                ausfall.fahrzeugId == fahrzeug.id && ausfall.von <= jetzt && ausfall.bis >= jetzt
            }

            val istBesetzt = buchungen.any { buchung ->
                buchung.fahrzeugId == fahrzeug.id &&
                        buchung.status == BuchungStatus.AKTIV &&
                        buchung.von <= jetzt && buchung.bis >= jetzt
            }

            val status = when {
                hatAusfall -> FahrzeugStatus.WARTUNG
                istBesetzt -> FahrzeugStatus.BESETZT
                else -> FahrzeugStatus.FREI
            }

            val typ = typen.find { it.id == fahrzeug.fahrzeugTypId }
            FahrzeugMitStatus(fahrzeug, typ, status)
        }
    }
}

class FlotteViewModelFactory(
    private val fahrerDao: FahrerDao,
    private val fahrzeugDao: FahrzeugDao,
    private val fahrzeugTypDao: FahrzeugTypDao,
    private val buchungDao: BuchungDao,
    private val fahrerzuweisungDao: FahrerzuweisungDao,
    private val fahrerAusfallDao: FahrerAusfallDao,
    private val fahrzeugAusfallDao: FahrzeugAusfallDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlotteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlotteViewModel(
                fahrerDao, fahrzeugDao, fahrzeugTypDao,
                buchungDao, fahrerzuweisungDao, fahrerAusfallDao, fahrzeugAusfallDao
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}