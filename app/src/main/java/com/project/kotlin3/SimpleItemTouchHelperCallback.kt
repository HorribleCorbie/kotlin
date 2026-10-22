package com.project.kotlin3

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.project.kotlin3.UserAdapter.UserViewHolder

class SimpleItemTouchHelperCallback(private val mAdapter: ItemTouchHelperAdapter) :
    ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags  = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val dragFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromUser = viewHolder.itemView.tag as User
        val toUser = target.itemView.tag as User
        mAdapter.onItemMove(fromUser, toUser)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val user = viewHolder.itemView.tag as User
        when (direction){
            ItemTouchHelper.UP ->{
                mAdapter.onItemDismiss(user)
            }
            ItemTouchHelper.DOWN ->{
                mAdapter.onItemCreate(user, viewHolder.itemView)
            }
        }
    }
}
