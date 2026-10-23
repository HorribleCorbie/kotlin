package com.project.kotlin3.View

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.kotlin3.Interface.ItemTouchHelperAdapter
import com.project.kotlin3.R
import com.project.kotlin3.SimpleItemTouchHelperCallback
import com.project.kotlin3.TypeUser
import com.project.kotlin3.User
import com.project.kotlin3.Interface.UserActionListener
import com.project.kotlin3.UserAdapter
import com.project.kotlin3.UserRepository
import com.project.kotlin3.UserRepository.currentUser
import com.project.kotlin3.databinding.ActivityListChatsBinding


class ListActivity : AppCompatActivity() {
    lateinit var binding: ActivityListChatsBinding
    lateinit var adapter: UserAdapter

    var isProgramClick = false

    private val listener: UserRepository.UserListener = {printChats()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UserAdapter(object : UserActionListener {
            override fun onUserGetId(user: User) =
                Toast.makeText(this@ListActivity, "Пользователь : ${user.name}", Toast.LENGTH_SHORT).show()

            override fun onUserLike(user: User) = UserRepository.likeUser(user)

        })

        binding.recycleView.layoutManager = LinearLayoutManager(this)

        binding.recycleView.adapter = adapter

        val callback= SimpleItemTouchHelperCallback(object : ItemTouchHelperAdapter{
            override fun onItemDismiss(user: User) = UserRepository.onItemDismiss(user)

            override fun onItemCreate(user: User, view: View) = UserRepository.createUser(user, view)

            override fun onItemMove(fromUser: User, toUser: User) = UserRepository.moveUser(fromUser, toUser)
        })

        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.recycleView)

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
                    intend.putExtra("user", "currentUser")
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

    fun printChats(){
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

    override fun onResume() {
        super.onResume()
        printChats()
    }

    override fun onDestroy() {
        UserRepository.removeListener(listener)
        super.onDestroy()
    }
}