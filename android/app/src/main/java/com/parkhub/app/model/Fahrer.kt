package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class FahrerStatus {
    FREI, EINGESETZT, ABWESEND
}

@Entity(tableName = "fahrer")
data class Fahrer(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val vorname: String,
    val nachname: String,
    val lizenzNummer: String
)