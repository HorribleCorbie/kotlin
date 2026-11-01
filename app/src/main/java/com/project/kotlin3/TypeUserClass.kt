package com.project.kotlin3

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "type",
    indices = [Index(value = ["type"], unique = true)]
)
class TypeUserClass(val type: TypeUser) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}