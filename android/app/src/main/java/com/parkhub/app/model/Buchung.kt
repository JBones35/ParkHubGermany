package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class BuchungStatus {
    AKTIV, ABGESCHLOSSEN, STORNIERT
}

@Entity(
    tableName = "buchung",
    foreignKeys = [
        ForeignKey(
            entity = Stellplatz::class,
            parentColumns = ["id"],
            childColumns = ["stellplatzId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Fahrzeug::class,
            parentColumns = ["id"],
            childColumns = ["fahrzeugId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("stellplatzId"), Index("fahrzeugId")]
)
data class Buchung(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val stellplatzId: UUID,
    val logistikId: UUID,
    val fahrzeugId: UUID,
    val von: Long,
    val bis: Long,
    val status: BuchungStatus = BuchungStatus.AKTIV
)