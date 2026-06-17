package com.parkhub.app.data

import androidx.room.*
import com.parkhub.app.model.Fahrerzuweisung
import kotlinx.coroutines.flow.Flow

@Dao
interface FahrerzuweisungDao {
    @Query("SELECT * FROM fahrerzuweisung")
    fun getAll(): Flow<List<Fahrerzuweisung>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(liste: List<Fahrerzuweisung>)

    @Delete
    suspend fun delete(zuweisung: Fahrerzuweisung): Int
}