package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "fahrzeug_ausfall",
    foreignKeys = [
        ForeignKey(
            entity = Fahrzeug::class,
            parentColumns = ["id"],
            childColumns = ["fahrzeugId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fahrzeugId")]
)
data class FahrzeugAusfall(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val fahrzeugId: UUID,
    val von: Long,
    val bis: Long,
    val grund: String
)