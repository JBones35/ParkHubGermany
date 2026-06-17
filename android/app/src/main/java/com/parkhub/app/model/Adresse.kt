package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "adresse")
data class Adresse(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val strasse: String,
    val hausnummer: String,
    val plz: String,
    val ort: String,
    val land: String = "Deutschland"
)

fun Adresse.vollständig(): String = "$strasse $hausnummer, $plz $ort"