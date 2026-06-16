package com.parkhub.app.model

data class StellplatzVorschau(
    val id: Int,
    val name: String,
    val entfernung: String,
    val preisProStunde: Double,
    val bewertung: Float,
    val anzahlBewertungen: Int
)

val stellplatzVorschauListe = listOf(
    StellplatzVorschau(1, "Hauptstraße 18", "350 m", 3.40, 4.8f, 38),
    StellplatzVorschau(2, "Kaiserstraße 142", "520 m", 4.20, 4.5f, 21),
    StellplatzVorschau(3, "Sophienstraße 25", "780 m", 2.80, 4.2f, 15),
    StellplatzVorschau(4, "Yorckstraße 33", "1,1 km", 3.80, 4.6f, 29),
    StellplatzVorschau(5, "Erbprinzenstraße 7", "1,4 km", 5.20, 4.9f, 44),
)