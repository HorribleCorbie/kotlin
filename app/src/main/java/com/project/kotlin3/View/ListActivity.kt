package com.project.kotlin3.View

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.kotlin3.FileExtension
import com.project.kotlin3.FileManager
import com.project.kotlin3.Interface.ItemTouchHelperAdapter
import com.project.kotlin3.Interface.UserActionListener
import com.project.kotlin3.R
import com.project.kotlin3.SimpleItemTouchHelperCallback
import com.project.kotlin3.TypeRepository
import com.project.kotlin3.TypeUser
import com.project.kotlin3.User
import com.project.kotlin3.UserAdapter
import com.project.kotlin3.UserRepository
import com.project.kotlin3.UserRepository.currentUser
import com.project.kotlin3.UserRoomDatabase
import com.project.kotlin3.databinding.ActivityListChatsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ListActivity : AppCompatActivity() {
    lateinit var binding: ActivityListChatsBinding
    lateinit var adapter: UserAdapter

    var isProgramClick = false

    private val PREFS_FILE: String = "Account"
    private val PREF_NAME: String = "Liked"
    var settings: SharedPreferences? = null

    private val listener: UserRepository.UserListener = { printChats() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE)

        adapter = UserAdapter(object : UserActionListener {
            override fun onUserGetId(user: User) =
                Toast.makeText(this@ListActivity, "Пользователь : ${user.name}", Toast.LENGTH_SHORT)
                    .show()

            override fun onUserLike(user: User) = UserRepository.likeUser(user)

        })

        binding.recycleView.layoutManager = LinearLayoutManager(this)

        binding.recycleView.adapter = adapter

        val callback = SimpleItemTouchHelperCallback(object : ItemTouchHelperAdapter {
            override fun onItemDismiss(user: User) = UserRepository.onItemDismiss(user)

            override fun onItemCreate(user: User, view: View) =
                UserRepository.createUser(user, view)

            override fun onItemMove(fromUser: User, toUser: User) =
                UserRepository.moveUser(fromUser, toUser)
        })

        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.recycleView)

        lifecycleScope.launch {

            val userDb = UserRoomDatabase.getInstance(application)

            withContext(Dispatchers.IO) {

                val userDao = userDb.userDao()
                val typeDao = userDb.typeDao()

                TypeRepository.init(typeDao)

                if (currentUser == null) {
                    val nickname = intent.getStringExtra("name") ?: "Undefined"

                    val phoneId = TypeRepository.returnId(TypeUser.Phone)!!

                    UserRepository.init(nickname, phoneId, userDao)
                }
            }

            UserRepository.addListener(listener)
        }


        binding.bottomNavigation.menu[1].isChecked = true

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
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

    fun printChats() {
        var newList: List<User> = UserRepository.getUsers()

        if (!binding.selectPC.isChecked) {
            newList = newList.filter { TypeRepository.returnType(it.type) != TypeUser.PC }
        }
        if (!binding.selectPhone.isChecked) {
            newList = newList.filter { TypeRepository.returnType(it.type) != TypeUser.Phone }
        }
        if (!binding.selectTablet.isChecked) {
            newList = newList.filter { TypeRepository.returnType(it.type) != TypeUser.Tablet }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }

    fun saveUsers() {
        val users = UserRepository.getLikedUsers()
        users.isEmpty().let {
            if (!it) {
                val set: Set<String> = users.map { it.name }.toSet()
                settings!!.edit { putStringSet(PREF_NAME, set) }
                Toast.makeText(this@ListActivity, "Сохранены настройки", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(
                    this@ListActivity,
                    "У вас нет избранных для сохранения",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    fun deleteUser() {
        if (settings != null && settings!!.contains(PREF_NAME)) {
            settings!!.edit { remove(PREF_NAME) }
            Toast.makeText(this@ListActivity, "Настройки удалены", Toast.LENGTH_SHORT).show()
        } else
            Toast.makeText(this@ListActivity, "У вас нет сохраненных настроек", Toast.LENGTH_SHORT)
                .show()
    }

    fun getUsers() {
        if (settings != null && settings!!.contains(PREF_NAME)) {
            val set: Set<String>? = settings!!.getStringSet(PREF_NAME, emptySet())
            if (set != null) {
                UserRepository.removeLikedUsers()
                for (i in set) {
                    UserRepository.likeUser(i)
                }
            }
        } else
            Toast.makeText(this@ListActivity, "У вас нет сохраненных настроек", Toast.LENGTH_SHORT)
                .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.save -> saveUsers()
            R.id.delete -> deleteUser()
            R.id.download -> getUsers()
            R.id.save_CSV -> {
                val f = FileManager(this@ListActivity, FileExtension.csv)
                f.createFile(UserRepository.printListFile())
            }

            R.id.load_csv -> {
                val f = FileManager(this@ListActivity, FileExtension.csv)
                val c = f.openFile()
                if (c != null){
                    UserRepository.fromFileInList(c)
                    printChats()
                }
                else
                    Toast.makeText(this@ListActivity, "Нечего загружать", Toast.LENGTH_SHORT).show()
            }

            R.id.delete_CSV -> {
                val f = FileManager(this@ListActivity, FileExtension.csv)
                val c = f.deleteFile()
                Toast.makeText(this@ListActivity, c, Toast.LENGTH_SHORT).show()
            }

            R.id.save_pdf -> {
                val f = FileManager(this@ListActivity, FileExtension.pdf)
                f.createFile(UserRepository.printListFile())
            }

            R.id.load_pdf -> {
                val f = FileManager(this@ListActivity, FileExtension.pdf)
                val c = f.openFile()
                if (c != null) {
                    UserRepository.fromFileInList(c)
                    printChats()
                }
                else
                    Toast.makeText(this@ListActivity, "Нечего загружать", Toast.LENGTH_SHORT).show()
            }

            R.id.delete_PDF -> {
                val f = FileManager(this@ListActivity, FileExtension.pdf)
                val c = f.deleteFile()
                Toast.makeText(this@ListActivity, c, Toast.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}