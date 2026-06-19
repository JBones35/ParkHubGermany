package com.parkhub.app.data

import androidx.room.*
import com.parkhub.app.model.Fahrzeug
import kotlinx.coroutines.flow.Flow

@Dao
interface FahrzeugDao {

    @Query("SELECT * FROM fahrzeug")
    fun getAllFahrzeug(): Flow<List<Fahrzeug>>

    @Query("SELECT * FROM fahrzeug WHERE (:status IS NULL OR status = :status)")
    fun getAllFahrzeugByStatus(status: String?): Flow<List<Fahrzeug>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(liste: List<Fahrzeug>)

    @Update
    suspend fun update(fahrzeug: Fahrzeug): Int

    @Delete
    suspend fun delete(fahrzeug: Fahrzeug): Int
}