package com.parkhub.app.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class FahrzeugStatus {
    AKTIV, WARTUNG
}

@Entity(
    tableName = "fahrzeug",
    foreignKeys = [
        ForeignKey(
            entity = FahrzeugTyp::class,
            parentColumns = ["id"],
            childColumns = ["fahrzeugTypId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("fahrzeugTypId")]
)
data class Fahrzeug(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val kennzeichen: String,
    val fahrzeugTypId: UUID,
    val status: FahrzeugStatus = FahrzeugStatus.AKTIV
)

val sprinter = FahrzeugTyp(UUID.randomUUID(), "Mercedes Sprinter", 200f, 540f, 210f, "3,5 t")
val crafter = FahrzeugTyp(UUID.randomUUID(), "VW Crafter", 198f, 530f, 205f, "3,5 t")
val daily = FahrzeugTyp(UUID.randomUUID(), "Iveco Daily", 202f, 550f, 215f, "3,5 t")
val transit = FahrzeugTyp(UUID.randomUUID(), "Ford Transit", 199f, 520f, 208f, "3,5 t")

val fahrzeugTypListe = listOf(sprinter, crafter, daily, transit)

// Sample Fahrzeuge
val fahrzeugListe = listOf(
    Fahrzeug(UUID.randomUUID(), "KA-XY 1234", sprinter.id, FahrzeugStatus.AKTIV),
    Fahrzeug(UUID.randomUUID(), "KA-HE 4421", crafter.id, FahrzeugStatus.AKTIV),
    Fahrzeug(UUID.randomUUID(), "KA-DH 8801", daily.id, FahrzeugStatus.WARTUNG),
    Fahrzeug(UUID.randomUUID(), "KA-DH 9012", transit.id, FahrzeugStatus.AKTIV),
)