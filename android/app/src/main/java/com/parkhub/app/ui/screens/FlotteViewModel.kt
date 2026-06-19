package com.parkhub.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parkhub.app.data.FahrerDao
import com.parkhub.app.data.FahrzeugDao
import com.parkhub.app.data.FahrzeugTypDao
import com.parkhub.app.model.Fahrer
import com.parkhub.app.model.FahrerStatus
import com.parkhub.app.model.Fahrzeug
import com.parkhub.app.model.FahrzeugStatus
import com.parkhub.app.model.FahrzeugTyp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

data class FahrzeugMitTyp(
    val fahrzeug: Fahrzeug,
    val typ: FahrzeugTyp?
)

class FlotteViewModel(
    private val fahrerDao: FahrerDao,
    private val fahrzeugDao: FahrzeugDao,
    private val fahrzeugTypDao: FahrzeugTypDao
) : ViewModel() {

    private val _fahrerStatusFilter = MutableStateFlow<FahrerStatus?>(null)
    val fahrerStatusFilter: StateFlow<FahrerStatus?> = _fahrerStatusFilter

    private val _fahrzeugStatusFilter = MutableStateFlow<FahrzeugStatus?>(null)
    val fahrzeugStatusFilter: StateFlow<FahrzeugStatus?> = _fahrzeugStatusFilter

    fun setFahrerStatusFilter(status: FahrerStatus?) {
        _fahrerStatusFilter.value = status
    }

    fun setFahrzeugStatusFilter(status: FahrzeugStatus?) {
        _fahrzeugStatusFilter.value = status
    }

    val fahrerList: Flow<List<Fahrer>> =
        _fahrerStatusFilter.flatMapLatest { status ->
            fahrerDao.getAllFahrerByStatus(status?.name)
        }

    val fahrzeugMitTypListe: Flow<List<FahrzeugMitTyp>> =
        _fahrzeugStatusFilter.flatMapLatest { status ->
            combine(
                fahrzeugDao.getAllFahrzeugByStatus(status?.name),
                fahrzeugTypDao.getAll()
            ) { fahrzeuge, typen ->
                fahrzeuge.map { fahrzeug ->
                    FahrzeugMitTyp(
                        fahrzeug = fahrzeug,
                        typ = typen.find { it.id == fahrzeug.fahrzeugTypId }
                    )
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