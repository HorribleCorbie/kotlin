package com.project.kotlin3

import android.content.Intent
import android.util.Log
import android.view.View
import com.project.kotlin3.Interface.UserDao
import com.project.kotlin3.View.AccountActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections

object UserRepository{
    var currentUser: User? = null
    var selectedUser: User? = null
    var userDao: UserDao? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

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

    private var users: List<User> = emptyList()
    private fun notifyChanges() {
        listeners.forEach {
            it.invoke(users)
        }
    }

    fun init(name: String, type: Int, userD: UserDao) {
        currentUser = User(name, type, false)
        userDao = userD
        users = userDao!!.getUsers()
    }

    fun clear() {
        selectedUser = null
    }

    fun getUsers(): List<User> {
        return users
    }

    fun getLikedUsers(): List<User> {
        return users.filter { it.isLiked }
    }

    fun  removeLikedUsers(){
        val likedUsers = getLikedUsers()
        likedUsers.forEach { it.isLiked=false
            coroutineScope.launch(Dispatchers.IO) {
                userDao?.updateUser(it)
            }
        }
    }

    fun likeUser(user: User) {
        val index = users.indexOfFirst { it.id == user.id }

        if (index != -1) {
            users[index].isLiked = !users[index].isLiked

            updateUsers {
                it.updateUser(users[index])
            }
        }
    }

    fun likeUser(name: String) {
        val index = users.indexOfFirst { it.name == name }

        if (index != -1) {
            users[index].isLiked = !users[index].isLiked

            updateUsers {
                it.updateUser(users[index])
            }
        }
    }


    fun onItemDismiss(user: User) {
        updateUsers {
            it.deleteUser(user)
        }
    }

    fun moveUser(fromUser: User, toUser: User) {
        val oldIndex = users.indexOfFirst { it.name == fromUser.name }
        val newIndex = users.indexOfFirst { it.name == toUser.name }
        if (oldIndex != -1 && newIndex!=-1) {
            users = ArrayList(users)
            Collections.swap(users, oldIndex, newIndex)
        }
    }


    fun createUser(fromUser: User, view: View){
        val index = users.indexOfFirst { it.name == fromUser.name }
        val intend = Intent(view.context, AccountActivity::class.java)
        intend.putExtra("user", "newUser")
        intend.putExtra("index", index)
        view.context.startActivity(intend)
    }

    fun createUser() {
        selectedUser?.let { user ->
            updateUsers {
                it.addUser(user)
            }
        }

        clear()
    }

    fun changeUser(user: User) {
        user.change(
            selectedUser?.name ?: user.name,
            selectedUser?.type ?: user.type
        )

        updateUsers {
            it.updateUser(user)
        }

        clear()
    }

    fun printListFile():String{
        val list = getUsers().map { it.toString() }.joinToString(separator = "\n")
        return list
    }

    fun fromFileInList(list: List<String>){
        val names = users.mapTo(mutableSetOf()) { it.name }
        val newUsers =  list.map { User(it) }
        val newNames = newUsers.mapTo(mutableSetOf()) { it.name }
        val deletedUsers = users.filter { !newNames.contains(it.name) }
        val u = newUsers.filter { !names.contains(it.name) } as MutableList<User>
        updateUsers {
            it.addUsers(u)
        }
        updateUsers {
            it.deleteUsers(deletedUsers)
        }
    }

    private fun updateUsers(action: (UserDao) -> Unit) {
        val dao = userDao ?: return

        coroutineScope.launch {
            val updatedUsers = withContext(Dispatchers.IO) {
                action(dao)
                dao.getUsers()
            }

            users = updatedUsers

            listeners.forEach {
                it.invoke(users)
            }
        }
    }
}