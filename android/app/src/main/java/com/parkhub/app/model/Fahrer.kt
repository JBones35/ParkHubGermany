package com.parkhub.app.model
import androidx.room.Entity
import androidx.room.PrimaryKey
enum class FahrerStatus {
    FREI,
    EINGESETZT,
    ABWESEND
}
@Entity(tableName = "fahrer")
data class Fahrer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vorname: String,
    val nachname: String,
    val lizenzNummer: String,
    val status: FahrerStatus = FahrerStatus.FREI
)
// Sample data
val fahrerListe = listOf(
    Fahrer(1, "Max", "Müller", "DE123456", FahrerStatus.FREI),
    Fahrer(2, "Anna", "Schmidt", "DE654321", FahrerStatus.FREI),
    Fahrer(3, "Paul", "Weber", "DE789012", FahrerStatus.FREI),
    Fahrer(4, "Lisa", "Meyer", "DE345678", FahrerStatus.ABWESEND),
    Fahrer(5, "Tom", "Wagner", "DE901234", FahrerStatus.FREI),
    Fahrer(6, "Julia", "König", "DE567890", FahrerStatus.FREI),
    Fahrer(7, "Marco", "Lange", "DE234567", FahrerStatus.FREI),
    Fahrer(8, "Sophie", "Hoffmann", "DE567123", FahrerStatus.FREI),
    Fahrer(9, "Oliver", "Becker", "DE890567", FahrerStatus.EINGESETZT),
)
