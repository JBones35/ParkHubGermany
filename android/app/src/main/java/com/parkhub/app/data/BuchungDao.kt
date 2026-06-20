package com.parkhub.app.data

import androidx.room.*
import com.parkhub.app.model.Buchung
import kotlinx.coroutines.flow.Flow

@Dao
interface BuchungDao {
    @Query("SELECT * FROM buchung")
    fun getAll(): Flow<List<Buchung>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(liste: List<Buchung>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(buchung: Buchung)

    @Delete
    suspend fun delete(buchung: Buchung): Int
}