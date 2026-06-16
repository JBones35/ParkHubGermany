package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class FahrzeugStatus {
    AKTIV, WARTUNG
}

@Entity(tableName = "fahrzeug")
data class Fahrzeug(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val kennzeichen: String,
    val modell: String,
    val gewicht: String,
    val breite_cm: Float = 0f,
    val laenge_cm: Float = 0f,
    val hoehe_cm: Float = 0f,
    val status: FahrzeugStatus = FahrzeugStatus.AKTIV
)

val fahrzeugListe = listOf(
    Fahrzeug(UUID.randomUUID(), "KA-XY 1234", "Mercedes Sprinter", "3,5 t", 200f, 540f, 210f, FahrzeugStatus.AKTIV),
    Fahrzeug(UUID.randomUUID(), "KA-HE 4421", "VW Crafter", "3,5 t", 198f, 530f, 205f, FahrzeugStatus.AKTIV),
    Fahrzeug(UUID.randomUUID(), "KA-DH 8801", "Iveco Daily", "3,5 t", 202f, 550f, 215f, FahrzeugStatus.WARTUNG),
    Fahrzeug(UUID.randomUUID(), "KA-DH 9012", "Ford Transit", "3,5 t", 199f, 520f, 208f, FahrzeugStatus.AKTIV),
)