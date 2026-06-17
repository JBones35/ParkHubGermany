package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "stellplatz_fahrzeugtyp",
    primaryKeys = ["stellplatzId", "fahrzeugtypId"],
    foreignKeys = [
        ForeignKey(
            entity = Stellplatz::class,
            parentColumns = ["id"],
            childColumns = ["stellplatzId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FahrzeugTyp::class,
            parentColumns = ["id"],
            childColumns = ["fahrzeugtypId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("stellplatzId"), Index("fahrzeugtypId")]
)
data class StellplatzFahrzeugtyp(
    val stellplatzId: UUID,
    val fahrzeugtypId: UUID
)