package com.parkhub.app.data

import androidx.room.*
import com.parkhub.app.model.Fahrer
import kotlinx.coroutines.flow.Flow

@Dao
interface FahrerDao {
    @Query("SELECT * FROM fahrer")
    fun getAllFahrer(): Flow<List<Fahrer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(liste: List<Fahrer>)

    @Delete
    suspend fun delete(fahrer: Fahrer): Int
}