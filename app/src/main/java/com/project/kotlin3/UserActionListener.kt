package com.project.kotlin3

interface UserActionListener {
    fun onUserGetId(user: User)
    fun onUserLike(user: User)
    fun onUserRemove(user: User)
    fun onUserMove(user: User, moveBy: Int)
}