package com.project.kotlin3

import androidx.room.TypeConverter

class TypeConverter {

    @TypeConverter
    fun fromTypeUser(type: TypeUser): String {
        return type.name
    }

    @TypeConverter
    fun toTypeUser(value: String): TypeUser {
        return TypeUser.valueOf(value)
    }
}