package com.parkhub.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.parkhub.app.data.AdresseDao
import com.parkhub.app.data.BewertungDao
import com.parkhub.app.data.FahrzeugTypDao
import com.parkhub.app.data.StellplatzDao
import com.parkhub.app.model.Adresse
import com.parkhub.app.model.FahrzeugTyp
import com.parkhub.app.model.Stellplatz
import com.parkhub.app.model.adresseListe
import com.parkhub.app.model.bewertungListe
import com.parkhub.app.model.fahrzeugTypListe
import com.parkhub.app.model.stellplatzListe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class StellplatzFilter(
    val minPreis: Float = 2.0f,
    val maxPreis: Float = 8.0f,
    val minBewertung: Float = 0f,
    val fahrzeugTyp: FahrzeugTyp? = null,
    val von: Long = System.currentTimeMillis(),
    val bis: Long = System.currentTimeMillis() + 2 * 60 * 60 * 1000 // +2h Default
)

data class StellplatzMitDetails(
    val stellplatz: Stellplatz,
    val adresse: Adresse?,
    val bewertungSchnitt: Float,
    val anzahlBewertungen: Int,
    val entfernungMeter: Int
)

class SucheViewModel(
    private val stellplatzDao: StellplatzDao,
    private val adresseDao: AdresseDao,
    private val bewertungDao: BewertungDao,
    private val fahrzeugTypDao: FahrzeugTypDao
) : ViewModel() {

    val fahrzeugTypListeFlow: Flow<List<FahrzeugTyp>> = fahrzeugTypDao.getAll()

    private val _filter = MutableStateFlow(StellplatzFilter())
    val filter: StateFlow<StellplatzFilter> = _filter

    fun updateFilter(neuerFilter: StellplatzFilter) {
        _filter.value = neuerFilter
    }

    private val gefilterteStellplaetze: Flow<List<Stellplatz>> =
        _filter.flatMapLatest { f ->
            stellplatzDao.getGefiltert(
                minFahrzeugLaenge = f.fahrzeugTyp?.laenge_cm ?: 0f,
                minFahrzeugBreite = f.fahrzeugTyp?.breite_cm ?: 0f,
                minFahrzeugHoehe = f.fahrzeugTyp?.hoehe_cm ?: 0f,
                minPreis = f.minPreis,
                maxPreis = f.maxPreis,
                minBewertung = f.minBewertung,
                von = f.von,
                bis = f.bis
            )
        }

    private val basisDaten: Flow<List<Triple<Stellplatz, Adresse?, Pair<Float, Int>>>> =
        combine(
            gefilterteStellplaetze,
            adresseDao.getAll(),
            bewertungDao.getAll()
        ) { stellplaetze, adressen, bewertungen ->
            stellplaetze.map { stellplatz ->
                val adresse = adressen.find { it.id == stellplatz.adresseId }
                val passendeBewertungen = bewertungen.filter { it.stellplatzId == stellplatz.id }
                val schnitt = if (passendeBewertungen.isNotEmpty())
                    passendeBewertungen.map { it.sterne }.average().toFloat()
                else 0f
                Triple(stellplatz, adresse, schnitt to passendeBewertungen.size)
            }
        }

    fun stellplaetzeMitEntfernung(suchLat: Double, suchLng: Double, radiusMeter: Int = 2000): Flow<List<StellplatzMitDetails>> =
        basisDaten.map { liste ->
            liste.map { (stellplatz, adresse, bewertungInfo) ->
                StellplatzMitDetails(
                    stellplatz = stellplatz,
                    adresse = adresse,
                    bewertungSchnitt = bewertungInfo.first,
                    anzahlBewertungen = bewertungInfo.second,
                    entfernungMeter = haversineMeter(
                        suchLat, suchLng,
                        stellplatz.gps_lat.toDouble(), stellplatz.gps_lng.toDouble()
                    )
                )
            }.filter { it.entfernungMeter <= radiusMeter }
        }

    private fun haversineMeter(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Int {
        val erdradius = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (erdradius * c).toInt()
    }
}

class SucheViewModelFactory(
    private val stellplatzDao: StellplatzDao,
    private val adresseDao: AdresseDao,
    private val bewertungDao: BewertungDao,
    private val fahrzeugTypDao: FahrzeugTypDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SucheViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SucheViewModel(stellplatzDao, adresseDao, bewertungDao, fahrzeugTypDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}