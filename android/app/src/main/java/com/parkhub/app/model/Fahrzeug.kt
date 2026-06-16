package com.parkhub.app.model

data class Fahrzeug(
    val kennzeichen: String,
    val modell: String,
    val gewicht: String,
    val status: FahrzeugStatus
)

val fahrzeugListe = listOf(
    Fahrzeug("KA-XY 1234", "Mercedes Sprinter", "3,5 t", FahrzeugStatus.AKTIV),
    Fahrzeug("KA-HE 4421", "VW Crafter", "3,5 t", FahrzeugStatus.AKTIV),
    Fahrzeug("KA-DH 8801", "Iveco Daily", "3,5 t", FahrzeugStatus.WARTUNG),
    Fahrzeug("KA-DH 9012", "Ford Transit", "3,5 t", FahrzeugStatus.AKTIV),
)