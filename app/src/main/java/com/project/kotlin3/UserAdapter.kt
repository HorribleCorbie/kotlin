package com.project.kotlin3

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.project.kotlin3.UserRepository.selectedUser
import com.project.kotlin3.View.AccountActivity
import com.project.kotlin3.databinding.ItemChatBinding

class UserAdapter(private val userActionListener: UserActionListener) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(), View.OnClickListener {

    var data: List<User> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
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

        popupMenu.menu.add(0, 1, Menu.NONE, "Изменить")

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                1 -> {
                    selectedUser = user
                    val intend = Intent(view.context, AccountActivity::class.java)
                    intend.putExtra("user", "selectedUser")
                    view.context.startActivity(intend)
                }
            }
            return@setOnMenuItemClickListener true
        }

        popupMenu.show()
    }

    class UserViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)
}