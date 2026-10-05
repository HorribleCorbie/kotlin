package com.project.kotlin3

object UserRepository {
    var currentUser: User? = null

    fun init(name: String, type:  TypeUser){
        currentUser = User(name, type)
    }

    fun clear(){
        currentUser = null
    }
}