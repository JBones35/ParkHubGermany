package com.parkhub.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.parkhub.app.data.FahrerDao
import com.parkhub.app.data.FahrzeugDao
import com.parkhub.app.data.FahrzeugTypDao
import com.parkhub.app.model.Fahrer
import com.parkhub.app.model.FahrzeugTyp
import com.parkhub.app.model.Fahrzeug
import com.parkhub.app.model.fahrerListe
import com.parkhub.app.model.fahrzeugListe
import com.parkhub.app.model.fahrzeugTypListe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class FahrzeugMitTyp(
    val fahrzeug: Fahrzeug,
    val typ: FahrzeugTyp?
)

class FlotteViewModel(
    private val fahrerDao: FahrerDao,
    private val fahrzeugDao: FahrzeugDao,
    private val fahrzeugTypDao: FahrzeugTypDao
) : ViewModel() {

    val fahrerList: Flow<List<Fahrer>> = fahrerDao.getAllFahrer()

    val fahrzeugMitTypListe: Flow<List<FahrzeugMitTyp>> =
        combine(fahrzeugDao.getAllFahrzeug(), fahrzeugTypDao.getAll()) { fahrzeuge, typen ->
            fahrzeuge.map { fahrzeug ->
                FahrzeugMitTyp(
                    fahrzeug = fahrzeug,
                    typ = typen.find { it.id == fahrzeug.fahrzeugTypId }
                )
            }
        }

    init {
        viewModelScope.launch {
            if (fahrerDao.getAllFahrer().firstOrNull().isNullOrEmpty()) {
                fahrerDao.insertAll(fahrerListe)
            }
            if (fahrzeugTypDao.getAll().firstOrNull().isNullOrEmpty()) {
                fahrzeugTypDao.insertAll(fahrzeugTypListe)
            }
            if (fahrzeugDao.getAllFahrzeug().firstOrNull().isNullOrEmpty()) {
                fahrzeugDao.insertAll(fahrzeugListe)
            }
        }
    }
}

class FlotteViewModelFactory(
    private val fahrerDao: FahrerDao,
    private val fahrzeugDao: FahrzeugDao,
    private val fahrzeugTypDao: FahrzeugTypDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlotteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlotteViewModel(fahrerDao, fahrzeugDao, fahrzeugTypDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}