package com.parkhub.app.data

import androidx.room.*
import com.parkhub.app.model.Fahrzeug
import kotlinx.coroutines.flow.Flow

@Dao
interface FahrzeugDao {
    @Query("SELECT * FROM fahrzeug")
    fun getAllFahrzeug(): Flow<List<Fahrzeug>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(liste: List<Fahrzeug>)

    @Delete
    suspend fun delete(fahrzeug: Fahrzeug): Int
}