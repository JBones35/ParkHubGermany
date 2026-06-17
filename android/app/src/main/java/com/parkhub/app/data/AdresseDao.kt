package com.parkhub.app.data

import androidx.room.*
import com.parkhub.app.model.Adresse
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface AdresseDao {
    @Query("SELECT * FROM adresse")
    fun getAll(): Flow<List<Adresse>>

    @Query("SELECT * FROM adresse WHERE id = :id")
    suspend fun getById(id: UUID): Adresse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(liste: List<Adresse>)

    @Update
    suspend fun update(adresse: Adresse): Int

    @Delete
    suspend fun delete(adresse: Adresse): Int
}