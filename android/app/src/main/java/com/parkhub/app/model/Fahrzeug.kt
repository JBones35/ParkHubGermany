package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class FahrzeugStatus {
    FREI, WARTUNG, BESETZT
}

@Entity(
    tableName = "fahrzeug",
    foreignKeys = [
        ForeignKey(
            entity = FahrzeugTyp::class,
            parentColumns = ["id"],
            childColumns = ["fahrzeugTypId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("fahrzeugTypId")]
)
data class Fahrzeug(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val kennzeichen: String,
    val fahrzeugTypId: UUID
)

