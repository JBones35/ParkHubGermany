package com.parkhub.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.parkhub.app.data.FahrerDao
import com.parkhub.app.data.FahrzeugDao
import com.parkhub.app.model.Fahrer
import com.parkhub.app.model.Fahrzeug
import com.parkhub.app.model.fahrerListe
import com.parkhub.app.model.fahrzeugListe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class FlotteViewModel(
    private val fahrerDao: FahrerDao,
    private val fahrzeugDao: FahrzeugDao
) : ViewModel() {

    val fahrerList: Flow<List<Fahrer>> = fahrerDao.getAllFahrer()
    val fahrzeugList: Flow<List<Fahrzeug>> = fahrzeugDao.getAllFahrzeug()

    init {
        viewModelScope.launch {
            val currentFahrer = fahrerDao.getAllFahrer().firstOrNull() ?: emptyList()
            if (currentFahrer.isEmpty()) {
                fahrerDao.insertAll(fahrerListe)
            }

            val currentFahrzeug = fahrzeugDao.getAllFahrzeug().firstOrNull() ?: emptyList()
            if (currentFahrzeug.isEmpty()) {
                fahrzeugDao.insertAll(fahrzeugListe)
            }
        }
    }
}

class FlotteViewModelFactory(
    private val fahrerDao: FahrerDao,
    private val fahrzeugDao: FahrzeugDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlotteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlotteViewModel(fahrerDao, fahrzeugDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}