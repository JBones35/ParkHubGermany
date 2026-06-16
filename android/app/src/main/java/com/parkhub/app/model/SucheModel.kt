package com.parkhub.app.model

data class Stellplatz(
    val id: Int,
    val adresse: String,
    val preisProStunde: Double,
    val latitude: Double,
    val longitude: Double
)

val stellplatzListe = listOf(
    Stellplatz(1, "Hauptstraße 18", 3.40, 49.0069, 8.4037),
    Stellplatz(2, "Kaiserstraße 142", 4.20, 49.0089, 8.4010),
    Stellplatz(3, "Sophienstraße 25", 2.80, 49.0050, 8.4060)
)