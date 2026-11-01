package com.project.kotlin3

class User(var name: String, var type: TypeUser, var isLiked: Boolean) {
    fun change(name: String, type: TypeUser) {
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
        type = TypeUser.valueOf( raw[1]),
        isLiked = raw[2].toBoolean()
    )
}