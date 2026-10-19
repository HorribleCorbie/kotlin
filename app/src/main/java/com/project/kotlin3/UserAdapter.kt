package com.project.kotlin3

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.project.kotlin3.databinding.ItemChatBinding

class UserAdapter(private val userActionListener: UserActionListener) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(), View.OnClickListener {

    var data: List<User> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        view.root.setOnClickListener(this)
        view.more.setOnClickListener(this)
        view.like.setOnClickListener(this)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = data[position]

        with(holder.binding) {
            name.text = user.name

            when (user.type) {
                TypeUser.PC -> iconType.setImageResource(R.drawable.computer_white)
                TypeUser.Phone -> iconType.setImageResource(R.drawable.iphone_white)
                TypeUser.Tablet -> iconType.setImageResource(R.drawable.tablet_white)
            }
            if (user.isLiked)
                like.setImageResource(R.drawable.baseline_bookmark_24)
            else
                like.setImageResource(R.drawable.outline_bookmark_24)
        }

        holder.itemView.tag = user
        holder.binding.like.tag = user
        holder.binding.more.tag = user
    }

    override fun getItemCount(): Int =data.size

    override fun onClick(view: View) {
        val user: User = view.tag as User

        when (view.id) {
            R.id.more -> showPopupMenu(view)
            R.id.like -> userActionListener.onUserLike(user)
            else -> userActionListener.onUserGetId(user)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val user = view.tag as User
        val position = data.indexOfFirst { it.name == user.name }

        popupMenu.menu.add(0, ID_MOVE_UP, Menu.NONE, "Up").apply {
            isEnabled = position > 0
        }
        popupMenu.menu.add(0, ID_MOVE_DOWN, Menu.NONE, "Down").apply {
            isEnabled = position < data.size - 1
        }
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, "Remove")

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_MOVE_UP -> userActionListener.onUserMove(user, -1)
                ID_MOVE_DOWN -> userActionListener.onUserMove(user, 1)
                ID_REMOVE -> userActionListener.onUserRemove(user)
            }
            return@setOnMenuItemClickListener true
        }

        popupMenu.show()
    }

    companion object {
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
    }

    class UserViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)
}