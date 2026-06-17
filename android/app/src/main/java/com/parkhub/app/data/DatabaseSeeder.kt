package com.parkhub.app.data

import com.parkhub.app.model.adresseListe
import com.parkhub.app.model.bewertungListe
import com.parkhub.app.model.buchungListe
import com.parkhub.app.model.fahrerListe
import com.parkhub.app.model.fahrerzuweisungListe
import com.parkhub.app.model.fahrzeugListe
import com.parkhub.app.model.fahrzeugTypListe
import com.parkhub.app.model.sperrzeitListe
import com.parkhub.app.model.stellplatzFahrzeugtypListe
import com.parkhub.app.model.stellplatzListe
import kotlinx.coroutines.flow.firstOrNull

class DatabaseSeeder(private val db: AppDatabase) {

    suspend fun seedIfEmpty() {
        if (db.fahrzeugTypDao().getAll().firstOrNull().isNullOrEmpty()) {
            db.fahrzeugTypDao().insertAll(fahrzeugTypListe)
        }

        if (db.fahrzeugDao().getAllFahrzeug().firstOrNull().isNullOrEmpty()) {
            db.fahrzeugDao().insertAll(fahrzeugListe)
        }

        if (db.fahrerDao().getAllFahrer().firstOrNull().isNullOrEmpty()) {
            db.fahrerDao().insertAll(fahrerListe)
        }

        if (db.adresseDao().getAll().firstOrNull().isNullOrEmpty()) {
            db.adresseDao().insertAll(adresseListe)
        }

        if (db.stellplatzDao().getAll().firstOrNull().isNullOrEmpty()) {
            db.stellplatzDao().insertAll(stellplatzListe)
        }

        if (db.stellplatzFahrzeugtypDao().getAll().firstOrNull().isNullOrEmpty()) {
            db.stellplatzFahrzeugtypDao().insertAll(stellplatzFahrzeugtypListe)
        }

        if (db.sperrzeitDao().getAll().firstOrNull().isNullOrEmpty()) {
            db.sperrzeitDao().insertAll(sperrzeitListe)
        }

        if (db.buchungDao().getAll().firstOrNull().isNullOrEmpty()) {
            db.buchungDao().insertAll(buchungListe)
        }

        if (db.bewertungDao().getAll().firstOrNull().isNullOrEmpty()) {
            db.bewertungDao().insertAll(bewertungListe)
        }

        if (db.fahrerzuweisungDao().getAll().firstOrNull().isNullOrEmpty()) {
            db.fahrerzuweisungDao().insertAll(fahrerzuweisungListe)
        }
    }
}