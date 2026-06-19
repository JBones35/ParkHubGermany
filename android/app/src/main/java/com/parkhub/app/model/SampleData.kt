package com.parkhub.app.model

import java.util.Calendar
import java.util.UUID

// ===== HILFSFUNKTION FÜR RELATIVE ZEITSTEMPEL =====
private fun heuteUm(stunde: Int, minute: Int = 0): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, stunde)
    cal.set(Calendar.MINUTE, minute)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

private fun inTagenUm(tage: Int, stunde: Int, minute: Int = 0): Long {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_MONTH, tage)
    cal.set(Calendar.HOUR_OF_DAY, stunde)
    cal.set(Calendar.MINUTE, minute)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

val sprinter = FahrzeugTyp(UUID.fromString("a1000000-0000-0000-0000-000000000001"), "Mercedes Sprinter", 200f, 540f, 210f, "3,5 t")
val crafter = FahrzeugTyp(UUID.fromString("a1000000-0000-0000-0000-000000000002"), "VW Crafter", 198f, 530f, 205f, "3,5 t")
val daily = FahrzeugTyp(UUID.fromString("a1000000-0000-0000-0000-000000000003"), "Iveco Daily", 236f, 640f, 280f, "5,0 t")
val transit = FahrzeugTyp(UUID.fromString("a1000000-0000-0000-0000-000000000004"), "Ford Transit", 199f, 520f, 208f, "3,5 t")

val fahrzeugTypListe = listOf(sprinter, crafter, daily, transit)

// ===== FAHRZEUG =====
val fahrzeug1 = Fahrzeug(UUID.fromString("a2000000-0000-0000-0000-000000000001"), "KA-XY 1234", sprinter.id, FahrzeugStatus.AKTIV)
val fahrzeug2 = Fahrzeug(UUID.fromString("a2000000-0000-0000-0000-000000000002"), "KA-HE 4421", crafter.id, FahrzeugStatus.AKTIV)
val fahrzeug3 = Fahrzeug(UUID.fromString("a2000000-0000-0000-0000-000000000003"), "KA-DH 8801", daily.id, FahrzeugStatus.WARTUNG)
val fahrzeug4 = Fahrzeug(UUID.fromString("a2000000-0000-0000-0000-000000000004"), "KA-DH 9012", transit.id, FahrzeugStatus.AKTIV)

val fahrzeugListe = listOf(fahrzeug1, fahrzeug2, fahrzeug3, fahrzeug4)

// ===== FAHRER =====
val fahrer1 = Fahrer(UUID.fromString("a3000000-0000-0000-0000-000000000001"), "Max", "Müller", "DE123456", FahrerStatus.EINGESETZT)
val fahrer2 = Fahrer(UUID.fromString("a3000000-0000-0000-0000-000000000002"), "Anna", "Schmidt", "DE654321", FahrerStatus.FREI)
val fahrer3 = Fahrer(UUID.fromString("a3000000-0000-0000-0000-000000000003"), "Paul", "Weber", "DE789012", FahrerStatus.FREI)
val fahrer4 = Fahrer(UUID.fromString("a3000000-0000-0000-0000-000000000004"), "Lisa", "Meyer", "DE345678", FahrerStatus.ABWESEND)
val fahrer5 = Fahrer(UUID.fromString("a3000000-0000-0000-0000-000000000005"), "Tom", "Wagner", "DE901234", FahrerStatus.FREI)
val fahrer6 = Fahrer(UUID.fromString("a3000000-0000-0000-0000-000000000006"), "Julia", "König", "DE567890", FahrerStatus.FREI)
val fahrer7 = Fahrer(UUID.fromString("a3000000-0000-0000-0000-000000000007"), "Marco", "Lange", "DE234567", FahrerStatus.FREI)
val fahrer8 = Fahrer(UUID.fromString("a3000000-0000-0000-0000-000000000008"), "Sophie", "Hoffmann", "DE567123", FahrerStatus.FREI)
val fahrer9 = Fahrer(UUID.fromString("a3000000-0000-0000-0000-000000000009"), "Oliver", "Becker", "DE890567", FahrerStatus.EINGESETZT)

val fahrerListe = listOf(
    fahrer1, fahrer2, fahrer3, fahrer4, fahrer5, fahrer6, fahrer7, fahrer8, fahrer9
)

// ===== ADRESSE =====
// adresse5 ist jetzt Heidelberg statt Karlsruhe
val adresse1 = Adresse(UUID.fromString("a4000000-0000-0000-0000-000000000001"), "Hauptstraße", "18", "76131", "Karlsruhe")
val adresse2 = Adresse(UUID.fromString("a4000000-0000-0000-0000-000000000002"), "Kaiserstraße", "142", "76133", "Karlsruhe")
val adresse3 = Adresse(UUID.fromString("a4000000-0000-0000-0000-000000000003"), "Sophienstraße", "25", "76135", "Karlsruhe")
val adresse4 = Adresse(UUID.fromString("a4000000-0000-0000-0000-000000000004"), "Yorckstraße", "33", "76131", "Karlsruhe")
val adresse5 = Adresse(UUID.fromString("a4000000-0000-0000-0000-000000000005"), "Hauptstraße", "120", "69117", "Heidelberg")

val adresseListe = listOf(adresse1, adresse2, adresse3, adresse4, adresse5)

// ===== STELLPLATZ =====
// stellplatz5 liegt jetzt in Heidelberg (Koordinaten entsprechend angepasst)
val stellplatz1 = Stellplatz(
    UUID.fromString("a5000000-0000-0000-0000-000000000001"), "Familie Becker", adresse1.id,
    250f, 600f, 220f, 3.40f, 49.0069f, 8.4037f
)
val stellplatz2 = Stellplatz(
    UUID.fromString("a5000000-0000-0000-0000-000000000002"), "Thomas Reiner", adresse2.id,
    240f, 580f, 215f, 4.20f, 49.0089f, 8.4010f
)
val stellplatz3 = Stellplatz(
    UUID.fromString("a5000000-0000-0000-0000-000000000003"), "Sabine Klein", adresse3.id,
    230f, 560f, 210f, 2.80f, 49.0050f, 8.4060f
)
val stellplatz4 = Stellplatz(
    UUID.fromString("a5000000-0000-0000-0000-000000000004"), "Michael Vogt", adresse4.id,
    260f, 620f, 225f, 3.80f, 49.0030f, 8.4090f
)
val stellplatz5 = Stellplatz(
    UUID.fromString("a5000000-0000-0000-0000-000000000005"), "Familie Krüger", adresse5.id,
    255f, 610f, 218f, 5.20f, 49.3988f, 8.6724f // Heidelberg-Koordinaten
)

val stellplatzListe = listOf(stellplatz1, stellplatz2, stellplatz3, stellplatz4, stellplatz5)

// ===== STELLPLATZ-FAHRZEUGTYP =====
val stellplatzFahrzeugtypListe = listOf(
    StellplatzFahrzeugtyp(stellplatz1.id, sprinter.id),
    StellplatzFahrzeugtyp(stellplatz1.id, crafter.id),
    StellplatzFahrzeugtyp(stellplatz2.id, daily.id),
    StellplatzFahrzeugtyp(stellplatz3.id, transit.id),
    StellplatzFahrzeugtyp(stellplatz3.id, sprinter.id),
    StellplatzFahrzeugtyp(stellplatz4.id, crafter.id),
    StellplatzFahrzeugtyp(stellplatz5.id, daily.id),
    StellplatzFahrzeugtyp(stellplatz5.id, transit.id),
)

// ===== SPERRZEIT =====
// Für jeden Tag der nächsten 20 Tage (heute = Tag 0 bis Tag 19) eine
// Sperrzeit von 0-7 Uhr auf stellplatz1. Per Schleife generiert statt
// 20 einzelne Variablen anzulegen.
val sperrzeitListe: List<Sperrzeit> = (0..19).map { tag ->
    Sperrzeit(
        id = UUID.fromString("a6000000-0000-0000-0000-${(tag + 1).toString().padStart(12, '0')}"),
        stellplatzId = stellplatz1.id,
        von = inTagenUm(tag, 0, 0),
        bis = inTagenUm(tag, 7, 0),
        grund = "Nächtliche Wartungsarbeiten"
    )
}

// ===== BUCHUNG =====
val logistikId1 = UUID.fromString("a7000000-0000-0000-0000-000000000001")
val logistikId2 = UUID.fromString("a7000000-0000-0000-0000-000000000002")

// Für jeden Tag der nächsten 20 Tage jeweils 3 Buchungen (14-18 Uhr)
// auf stellplatz1, stellplatz2, stellplatz3 - mit den Fahrzeugen 1, 2, 3.
val buchungZuordnung = listOf(
    Triple(stellplatz1.id, fahrzeug1.id, logistikId1),
    Triple(stellplatz2.id, fahrzeug2.id, logistikId1),
    Triple(stellplatz3.id, fahrzeug3.id, logistikId2)
)

val buchungListe: List<Buchung> = (0..19).flatMap { tag ->
    buchungZuordnung.mapIndexed { index, (stellplatzId, fahrzeugId, logistikId) ->
        Buchung(
            id = UUID.fromString("a8000000-0000-0000-${(tag + 1).toString().padStart(4, '0')}-${(index + 1).toString().padStart(12, '0')}"),
            stellplatzId = stellplatzId,
            logistikId = logistikId,
            fahrzeugId = fahrzeugId,
            von = inTagenUm(tag, 14, 0),
            bis = inTagenUm(tag, 18, 0),
            status = BuchungStatus.AKTIV
        )
    }
}

// ===== BEWERTUNG =====
val bewertung1 = Bewertung(
    UUID.fromString("a9000000-0000-0000-0000-000000000001"), stellplatz1.id, fahrer1.id,
    sterne = 5, kommentar = "Sehr zugänglich, viel Platz", erstelltAm = inTagenUm(-7, 10, 0)
)
val bewertung2 = Bewertung(
    UUID.fromString("a9000000-0000-0000-0000-000000000002"), stellplatz2.id, fahrer2.id,
    sterne = 4, kommentar = "Gute Lage, etwas eng beim Einparken", erstelltAm = inTagenUm(-5, 10, 0)
)
val bewertung3 = Bewertung(
    UUID.fromString("a9000000-0000-0000-0000-000000000003"), stellplatz3.id, fahrer3.id,
    sterne = 5, kommentar = "Top Vermieter, schnelle Kommunikation", erstelltAm = inTagenUm(-3, 10, 0)
)

val bewertungListe = listOf(bewertung1, bewertung2, bewertung3)

// ===== FAHRERZUWEISUNG =====
val fahrerzuweisung1 = Fahrerzuweisung(
    UUID.fromString("aa000000-0000-0000-0000-000000000001"), buchungListe[0].id, fahrer1.id, zugewiesenAm = inTagenUm(0, 13, 0)
)
val fahrerzuweisung2 = Fahrerzuweisung(
    UUID.fromString("aa000000-0000-0000-0000-000000000002"), buchungListe[1].id, fahrer2.id, zugewiesenAm = inTagenUm(0, 13, 0)
)
val fahrerzuweisung3 = Fahrerzuweisung(
    UUID.fromString("aa000000-0000-0000-0000-000000000003"), buchungListe[2].id, fahrer9.id, zugewiesenAm = inTagenUm(0, 13, 0)
)

val fahrerzuweisungListe = listOf(fahrerzuweisung1, fahrerzuweisung2, fahrerzuweisung3)