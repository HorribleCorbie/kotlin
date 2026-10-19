package com.project.kotlin3.View

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.kotlin3.R
import com.project.kotlin3.TypeUser
import com.project.kotlin3.User
import com.project.kotlin3.databinding.ActivityListChatsBinding
import androidx.core.graphics.toColorInt
import androidx.core.view.get
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.kotlin3.UserActionListener
import com.project.kotlin3.UserAdapter
import com.project.kotlin3.UserRepository
import com.project.kotlin3.UserRepository.currentUser

class ListActivity : AppCompatActivity() {
    lateinit var binding: ActivityListChatsBinding
    lateinit var adapter: UserAdapter

    var isProgramClick = false

    private val listener: UserRepository.UserListener = {adapter.data = it}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UserAdapter(object : UserActionListener {
            override fun onUserGetId(user: User) =
                Toast.makeText(this@ListActivity, "Persons Name: ${user.name}", Toast.LENGTH_SHORT).show()

            override fun onUserLike(user: User) = UserRepository.likeUser(user)

            override fun onUserRemove(user: User) = UserRepository.removeUser(user)

            override fun onUserMove(user: User, moveBy: Int) = UserRepository.moveUser(user, moveBy)

        })
        adapter.data = UserRepository.getUsers()

        binding.recycleView.layoutManager = LinearLayoutManager(this)
        binding.recycleView.adapter = adapter

        UserRepository.addListener(listener)

        if (currentUser==null) {
            val intent = getIntent()
            val nickname = intent.getStringExtra("name") ?: "Undefined"
            UserRepository.init(nickname, TypeUser.Phone)
        }


        binding.bottomNavigation.menu[1].isChecked = true

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.logout -> {
                    val intend = Intent(this, MainActivity::class.java)
                    intend.putExtra("name", currentUser?.name)
                    startActivity(intend)
                    finish()
                    true
                }
                R.id.home -> {
                    true
                }
                R.id.account -> {
                    val intend = Intent(this, AccountActivity::class.java)
                    startActivity(intend)
                    true
                }
                else -> {
                    false
                }
            }
        }

        val listCheckBox = listOf(
            binding.selectPC,
            binding.selectPhone,
            binding.selectTablet
        )

        binding.selectAll.setOnCheckedChangeListener { _, isChecked ->
            if (binding.selectAll.isChecked) {
                listCheckBox.forEach { it.isChecked = isChecked }
            } else if (!isProgramClick) {
                listCheckBox.forEach { it.isChecked = false }
            }
            isProgramClick = false
            printChats()
        }

        binding.selectAll.isChecked = true
        updateSelectAll()

        listCheckBox.forEach {
            it.setOnCheckedChangeListener { _, _ ->
                updateSelectAll()
                printChats()
            }
        }

        printChats()
    }

    fun updateSelectAll() {
        isProgramClick = true
        binding.selectAll.isChecked =
            binding.selectTablet.isChecked && binding.selectPC.isChecked && binding.selectPhone.isChecked
    }

    fun printChats() {
        var newList: List<User> = UserRepository.getUsers()

        if (!binding.selectPC.isChecked) {
            newList = newList.filter{it.type !=TypeUser.PC }
        }
        if (!binding.selectPhone.isChecked) {
            newList =newList.filter{it.type !=TypeUser.Phone }
        }
        if (!binding.selectTablet.isChecked) {
            newList = newList.filter{it.type !=TypeUser.Tablet }
        }

        adapter.data = newList
    }
}