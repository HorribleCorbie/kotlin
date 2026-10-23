package com.project.kotlin3.Interface

import android.view.View
import com.project.kotlin3.User

interface ItemTouchHelperAdapter {
    fun onItemMove(fromUser: User, toUser: User)

    fun onItemDismiss(user: User)

    fun onItemCreate(user: User, view: View)
}

interface UserActionListener {
    fun onUserGetId(user: User)
    fun onUserLike(user: User)
}