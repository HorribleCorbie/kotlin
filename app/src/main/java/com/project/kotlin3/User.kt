package com.project.kotlin3

class User(var name: String, var type: TypeUser) {
    fun change(name: String, type: TypeUser) {
        this.name = name
        this.type = type
    }
}