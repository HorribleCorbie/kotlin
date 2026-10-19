package com.project.kotlin3

import java.util.Collections

object UserRepository {
    var currentUser: User? = null
    var selectedUser: User? = null

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

    fun init(name: String, type: TypeUser) {
        currentUser = User(name, type, false)
    }

    fun clear() {
        currentUser = null
    }

    private var users = mutableListOf<User>()

    init {
        users = (1..50).map {
            User(
                name = "user" + it.toLong().toString(),
                type = TypeUser.entries[it % 3],
                isLiked = false
            )
        }.toMutableList()
    }

    fun getUsers(): MutableList<User> {
        return users
    }

    fun likeUser(user: User) {
        val index = users.indexOfFirst { it.name == user.name }
        if (index != -1) {
            users = ArrayList(users)
            if (users[index].isLiked)
                users[index].isLiked = false
            else
                users[index].isLiked = true
        }
        notifyChanges()
    }

    fun onItemDismiss(user: User) {
        users.remove(user)
        notifyChanges()
    }

    fun movePerson(fromUser: User, toUser: User) {
        val oldIndex = users.indexOfFirst { it.name == fromUser.name }
        val newIndex = users.indexOfFirst { it.name == toUser.name }
        if (oldIndex != -1 && newIndex!=-1) {
            users = ArrayList(users)
            Collections.swap(users, oldIndex, newIndex)
            notifyChanges()
        }
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition..<toPosition) {
                Collections.swap(users, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(users, i, i - 1)
            }
        }
        notifyChanges()
    }
}