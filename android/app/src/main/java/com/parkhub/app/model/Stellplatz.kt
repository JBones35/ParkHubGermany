package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class StellplatzStatus {
    FREI, BELEGT, RESERVIERT
}

@Entity(
    tableName = "stellplatz",
    foreignKeys = [
        ForeignKey(
            entity = Adresse::class,
            parentColumns = ["id"],
            childColumns = ["adresseId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("adresseId")]
)
data class Stellplatz(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val vermieter: String,
    val adresseId: UUID,
    val breite_cm: Float,
    val laenge_cm: Float,
    val hoehe_cm: Float,
    val preis_stunde: Float,
    val status: StellplatzStatus = StellplatzStatus.FREI,
    val gps_lat: Float,
    val gps_lng: Float
)