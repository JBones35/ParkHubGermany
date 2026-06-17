package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "sperrzeit",
    foreignKeys = [
        ForeignKey(
            entity = Stellplatz::class,
            parentColumns = ["id"],
            childColumns = ["stellplatzId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("stellplatzId")]
)
data class Sperrzeit(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val stellplatzId: UUID,
    val von: Long,      // Timestamp in Millisekunden
    val bis: Long,
    val grund: String
)