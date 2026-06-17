package com.parkhub.app.data

import androidx.room.*
import com.parkhub.app.model.Bewertung
import kotlinx.coroutines.flow.Flow

@Dao
interface BewertungDao {
    @Query("SELECT * FROM bewertung")
    fun getAll(): Flow<List<Bewertung>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(liste: List<Bewertung>)

    @Delete
    suspend fun delete(bewertung: Bewertung): Int
}