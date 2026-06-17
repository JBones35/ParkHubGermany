package com.parkhub.app.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class FahrerStatus {
    FREI,
    EINGESETZT,
    ABWESEND
}
@Entity(tableName = "fahrer")
data class Fahrer(
    @PrimaryKey(autoGenerate = true)
    val id: UUID,
    val vorname: String,
    val nachname: String,
    val lizenzNummer: String,
    val status: FahrerStatus = FahrerStatus.FREI
)

