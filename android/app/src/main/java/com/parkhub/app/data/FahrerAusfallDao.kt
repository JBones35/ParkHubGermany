package com.parkhub.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.parkhub.app.model.FahrerAusfall
import kotlinx.coroutines.flow.Flow

@Dao
interface FahrerAusfallDao {
    @Query("SELECT * FROM fahrer_ausfall")
    fun getAll(): Flow<List<FahrerAusfall>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(liste: List<FahrerAusfall>)

    @Delete
    suspend fun delete(ausfall: FahrerAusfall): Int
}