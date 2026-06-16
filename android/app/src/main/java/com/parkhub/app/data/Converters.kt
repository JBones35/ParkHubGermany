package com.parkhub.app.data
import androidx.room.TypeConverter
import com.parkhub.app.model.FahrerStatus
import com.parkhub.app.model.FahrzeugStatus
import java.util.UUID

class Converters {
    @TypeConverter
    fun fromFahrerStatus(value: String): FahrerStatus {
        return FahrerStatus.valueOf(value)
    }
    @TypeConverter
    fun fahrerStatusToString(status: FahrerStatus): String {
        return status.name
    }

    @TypeConverter
    fun fromFahrzeugStatus(status: FahrzeugStatus): String = status.name

    @TypeConverter
    fun toFahrzeugStatus(value: String): FahrzeugStatus = try {
        FahrzeugStatus.valueOf(value)
    } catch (e: Exception) {
        FahrzeugStatus.AKTIV
    }

    @TypeConverter
    fun fromUUID(uuid: UUID): String = uuid.toString()

    @TypeConverter
    fun toUUID(value: String): UUID = UUID.fromString(value)
}
