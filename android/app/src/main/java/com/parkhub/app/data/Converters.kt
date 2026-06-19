package com.parkhub.app.data
import androidx.room.TypeConverter
import com.parkhub.app.model.FahrerStatus
import com.parkhub.app.model.FahrzeugStatus
import java.util.UUID

class Converters {
    @TypeConverter
    fun fromUUID(uuid: UUID): String = uuid.toString()

    @TypeConverter
    fun toUUID(value: String): UUID = UUID.fromString(value)
}
