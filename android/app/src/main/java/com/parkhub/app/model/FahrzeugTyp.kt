package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "fahrzeug_typ")
data class FahrzeugTyp(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val bezeichnung: String,    // z.B. "Mercedes Sprinter"
    val breite_cm: Float,
    val laenge_cm: Float,
    val hoehe_cm: Float,
    val gewicht: String         // z.B. "3,5 t"
)