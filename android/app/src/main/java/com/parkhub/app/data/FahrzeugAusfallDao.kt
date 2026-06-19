package com.parkhub.app.data

import androidx.room.*
import com.parkhub.app.model.FahrzeugAusfall
import kotlinx.coroutines.flow.Flow

@Dao
interface FahrzeugAusfallDao {
    @Query("SELECT * FROM fahrzeug_ausfall")
    fun getAll(): Flow<List<FahrzeugAusfall>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(liste: List<FahrzeugAusfall>)

    @Delete
    suspend fun delete(ausfall: FahrzeugAusfall): Int
}