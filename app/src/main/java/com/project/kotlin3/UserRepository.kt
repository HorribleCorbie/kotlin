package com.project.kotlin3

import androidx.room.util.copy
import java.util.Collections

object UserRepository {
    var currentUser: User? = null

    typealias UserListener = (users: List<User>) -> Unit

    private var listeners = mutableListOf<UserListener>()

    fun addListener(listener: UserListener) {
        listeners.add(listener)
        listener.invoke(users)
    }

    fun removeListener(listener: UserListener) {
        listeners.remove(listener)
        listener.invoke(users)
    }

    private fun notifyChanges() = listeners.forEach { it.invoke(users) }

    fun init(name: String, type:  TypeUser){
        currentUser = User(name, type, false)
    }

    fun clear(){
        currentUser = null
    }

    private var users = mutableListOf<User>()

    init {
        users = (1..50).map {
            User(
                name ="user"+ it.toLong().toString(),
                type = TypeUser.entries[it % 3],
                isLiked = false
            )
        }.toMutableList()
    }

    fun getUsers(): MutableList<User> {
        return users
    }

    fun likeUser(user: User){
        val index = users.indexOfFirst{it.name == user.name}
        if (index!=-1){
            users = ArrayList(users)
            if (users[index].isLiked)
                users[index].isLiked = false
            else
                users[index].isLiked = true
        }
        notifyChanges()
    }

    fun removeUser(user: User){
        val index = users.indexOfFirst { it.name == user.name }
        if (index!=-1){
            users = ArrayList(users)
            users.removeAt(index)
        }
        notifyChanges()
    }

    fun moveUser(user: User, moveBy: Int){
        val index = users.indexOfFirst { it.name == user.name }
        if (index!=-1){
            val newIndex = index + moveBy
            users = ArrayList(users)
            Collections.swap(users, index, newIndex)
        }
        notifyChanges()
    }
}