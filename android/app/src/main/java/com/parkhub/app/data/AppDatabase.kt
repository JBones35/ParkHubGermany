package com.parkhub.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.parkhub.app.model.Bewertung
import com.parkhub.app.model.Buchung
import com.parkhub.app.model.Fahrer
import com.parkhub.app.model.Fahrerzuweisung
import com.parkhub.app.model.Fahrzeug
import com.parkhub.app.model.FahrzeugTyp
import com.parkhub.app.model.Sperrzeit
import com.parkhub.app.model.Stellplatz
import com.parkhub.app.model.Adresse
import com.parkhub.app.model.StellplatzFahrzeugtyp

@Database(
    entities = [
        Fahrer::class, Fahrzeug::class, FahrzeugTyp::class,
        Stellplatz::class, Sperrzeit::class, Buchung::class,
        Bewertung::class, StellplatzFahrzeugtyp::class, Fahrerzuweisung::class,
        Adresse::class
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fahrerDao(): FahrerDao
    abstract fun fahrzeugDao(): FahrzeugDao
    abstract fun fahrzeugTypDao(): FahrzeugTypDao
    abstract fun stellplatzDao(): StellplatzDao
    abstract fun sperrzeitDao(): SperrzeitDao
    abstract fun buchungDao(): BuchungDao
    abstract fun bewertungDao(): BewertungDao
    abstract fun stellplatzFahrzeugtypDao(): StellplatzFahrzeugtypDao
    abstract fun fahrerzuweisungDao(): FahrerzuweisungDao
    abstract fun adresseDao(): AdresseDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "parkhub.db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}