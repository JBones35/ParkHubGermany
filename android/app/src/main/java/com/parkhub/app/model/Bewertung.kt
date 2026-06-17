package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "bewertung",
    foreignKeys = [
        ForeignKey(
            entity = Stellplatz::class,
            parentColumns = ["id"],
            childColumns = ["stellplatzId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Fahrer::class,
            parentColumns = ["id"],
            childColumns = ["fahrerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("stellplatzId"), Index("fahrerId")]
)
data class Bewertung(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val stellplatzId: UUID,
    val fahrerId: UUID,
    val sterne: Int,
    val kommentar: String,
    val erstelltAm: Long
)