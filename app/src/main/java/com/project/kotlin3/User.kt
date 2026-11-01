package com.project.kotlin3

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["type"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = TypeUserClass::class,
            parentColumns = ["id"],
            childColumns = ["type"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)

class User(var name: String, var type: Int, var isLiked: Boolean) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun change(name: String, type: Int) {
        this.name = name
        this.type = type
    }
    override fun toString(): String {
        return "$name, $type, $isLiked"
    }

    constructor(raw: String): this(
        raw.split(",").map{ it.trim() }.also {  require(it.size==3){"Неверный ввод данных"} }
    )

    constructor(raw: List<String>): this(
        name = raw[0],
        type = raw[1].toInt(),
        isLiked = raw[2].toBoolean()
    )
}