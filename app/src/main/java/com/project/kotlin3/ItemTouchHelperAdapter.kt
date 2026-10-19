package com.project.kotlin3

interface ItemTouchHelperAdapter {
    fun onItemMove(fromUser: User, toUser: User)

    fun onItemDismiss(user: User)
}