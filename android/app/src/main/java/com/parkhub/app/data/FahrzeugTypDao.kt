package com.parkhub.app.data

import androidx.room.*
import com.parkhub.app.model.FahrzeugTyp
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface FahrzeugTypDao {

    @Query("SELECT * FROM fahrzeug_typ")
    fun getAll(): Flow<List<FahrzeugTyp>>

    @Query("SELECT * FROM fahrzeug_typ WHERE id = :id")
    suspend fun getById(id: UUID): FahrzeugTyp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(typen: List<FahrzeugTyp>)

    @Delete
    suspend fun delete(typ: FahrzeugTyp): Int
}