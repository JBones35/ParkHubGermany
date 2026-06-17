package com.parkhub.app.data

import androidx.room.*
import com.parkhub.app.model.Stellplatz
import kotlinx.coroutines.flow.Flow

@Dao
interface StellplatzDao {

    @Query("SELECT * FROM stellplatz")
    fun getAll(): Flow<List<Stellplatz>>

    @Query(
        """
        SELECT s.* FROM stellplatz s
        LEFT JOIN bewertung b ON b.stellplatzId = s.id
        WHERE s.laenge_cm >= :minFahrzeugLaenge
        AND s.breite_cm >= :minFahrzeugBreite
        AND s.hoehe_cm >= :minFahrzeugHoehe
        AND s.preis_stunde BETWEEN :minPreis AND :maxPreis
        AND NOT EXISTS (
            SELECT 1 FROM buchung buc
            WHERE buc.stellplatzId = s.id
            AND buc.status = 'AKTIV'
            AND buc.von < :bis
            AND buc.bis > :von
        )
        AND NOT EXISTS (
            SELECT 1 FROM sperrzeit sp
            WHERE sp.stellplatzId = s.id
            AND sp.von < :bis
            AND sp.bis > :von
        )
        GROUP BY s.id
        HAVING COALESCE(AVG(b.sterne), 0) >= :minBewertung
        """
    )
    fun getGefiltert(
        minFahrzeugLaenge: Float,
        minFahrzeugBreite: Float,
        minFahrzeugHoehe: Float,
        minPreis: Float,
        maxPreis: Float,
        minBewertung: Float,
        von: Long,
        bis: Long
    ): Flow<List<Stellplatz>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(liste: List<Stellplatz>)

    @Update
    suspend fun update(stellplatz: Stellplatz): Int

    @Delete
    suspend fun delete(stellplatz: Stellplatz): Int
}