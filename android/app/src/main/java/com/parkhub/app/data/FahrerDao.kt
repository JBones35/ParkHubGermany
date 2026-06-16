package com.parkhub.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.parkhub.app.model.Fahrer
import kotlinx.coroutines.flow.Flow

@Dao
interface FahrerDao {
    @Query("SELECT * FROM fahrer ORDER BY vorname ASC")
    fun getAllFahrer(): Flow<List<Fahrer>>
    @Query("SELECT COUNT(*) FROM fahrer")
    suspend fun count(): Long
    @Insert
    suspend fun insert(fahrer: Fahrer): Long
    @Insert
    suspend fun insertAll(fahrerliste: List<Fahrer>): List<Long>
    @Update
    suspend fun update(fahrer: Fahrer): Int
    @Delete
    suspend fun delete(fahrer: Fahrer): Int
}