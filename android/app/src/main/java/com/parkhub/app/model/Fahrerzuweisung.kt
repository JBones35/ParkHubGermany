package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "fahrerzuweisung",
    foreignKeys = [
        ForeignKey(
            entity = Buchung::class,
            parentColumns = ["id"],
            childColumns = ["buchungId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Fahrer::class,
            parentColumns = ["id"],
            childColumns = ["fahrerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("buchungId"), Index("fahrerId")]
)
data class Fahrerzuweisung(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val buchungId: UUID,
    val fahrerId: UUID,
    val zugewiesenAm: Long
)