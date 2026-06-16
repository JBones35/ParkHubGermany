package com.parkhub.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.parkhub.app.model.Fahrzeug
import kotlinx.coroutines.flow.Flow

@Dao
interface FahrzeugDao {

    @Query("SELECT * FROM fahrzeug")
    fun getAllFahrzeug(): Flow<List<Fahrzeug>>

    @Query("SELECT COUNT(*) FROM fahrzeug")
    suspend fun count(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fahrzeug: Fahrzeug): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fahrzeugListe: List<Fahrzeug>): List<Long>

    @Update
    suspend fun update(fahrzeug: Fahrzeug): Int

    @Delete
    suspend fun delete(fahrzeug: Fahrzeug): Int
}