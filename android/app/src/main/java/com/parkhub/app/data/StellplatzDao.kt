package com.parkhub.app.data

import androidx.room.*
import com.parkhub.app.model.Stellplatz
import kotlinx.coroutines.flow.Flow

@Dao
interface StellplatzDao {
    @Query("SELECT * FROM stellplatz")
    fun getAll(): Flow<List<Stellplatz>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(liste: List<Stellplatz>)

    @Update
    suspend fun update(stellplatz: Stellplatz): Int

    @Delete
    suspend fun delete(stellplatz: Stellplatz): Int
}