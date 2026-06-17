package com.parkhub.app.data

import androidx.room.*
import com.parkhub.app.model.Sperrzeit
import kotlinx.coroutines.flow.Flow

@Dao
interface SperrzeitDao {
    @Query("SELECT * FROM sperrzeit")
    fun getAll(): Flow<List<Sperrzeit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(liste: List<Sperrzeit>)

    @Delete
    suspend fun delete(sperrzeit: Sperrzeit): Int
}