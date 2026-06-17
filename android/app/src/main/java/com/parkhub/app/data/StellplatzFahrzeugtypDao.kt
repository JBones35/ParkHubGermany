package com.parkhub.app.data

import androidx.room.*
import com.parkhub.app.model.StellplatzFahrzeugtyp
import kotlinx.coroutines.flow.Flow

@Dao
interface StellplatzFahrzeugtypDao {
    @Query("SELECT * FROM stellplatz_fahrzeugtyp")
    fun getAll(): Flow<List<StellplatzFahrzeugtyp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(liste: List<StellplatzFahrzeugtyp>)

    @Delete
    suspend fun delete(eintrag: StellplatzFahrzeugtyp): Int
}