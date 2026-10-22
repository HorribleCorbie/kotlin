package com.project.kotlin3

import android.content.Intent
import android.view.View
import com.project.kotlin3.View.AccountActivity
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
        selectedUser = null
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

    fun moveUser(fromUser: User, toUser: User) {
        val oldIndex = users.indexOfFirst { it.name == fromUser.name }
        val newIndex = users.indexOfFirst { it.name == toUser.name }
        if (oldIndex != -1 && newIndex!=-1) {
            users = ArrayList(users)
            Collections.swap(users, oldIndex, newIndex)
            notifyChanges()
        }
    }


    fun createUser(fromUser: User, view: View){
        val index = users.indexOfFirst { it.name == fromUser.name }
        val intend = Intent(view.context, AccountActivity::class.java)
        intend.putExtra("user", "newUser")
        intend.putExtra("index", index)
        view.context.startActivity(intend)
    }

    fun createUser(index: Int){
        selectedUser?.let { users.add(index, it) }
        clear()
        notifyChanges()
    }

    fun changeUser(user:User){
        user.change(selectedUser?.name ?: user.name, selectedUser?.type ?: user.type)
        clear()
        notifyChanges()
    }
}