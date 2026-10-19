package com.project.kotlin3

interface UserActionListener {
    fun onUserGetId(user: User)
    fun onUserLike(user: User)
}