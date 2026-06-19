package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "fahrer_ausfall",
    foreignKeys = [
        ForeignKey(
            entity = Fahrer::class,
            parentColumns = ["id"],
            childColumns = ["fahrerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fahrerId")]
)
data class FahrerAusfall(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val fahrerId: UUID,
    val von: Long,
    val bis: Long,
    val grund: String
)