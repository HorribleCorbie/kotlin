package com.project.kotlin3

import android.view.View

interface ItemTouchHelperAdapter {
    fun onItemMove(fromUser: User, toUser: User)

    fun onItemDismiss(user: User)

    fun onItemCreate(user: User, view: View)
}