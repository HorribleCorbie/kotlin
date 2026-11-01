package com.project.kotlin3.View

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.project.kotlin3.User
import com.project.kotlin3.UserRepository
import com.project.kotlin3.UserRoomDatabase
import com.project.kotlin3.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var list: List<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        val intent = getIntent()
        val nickname = intent.getStringExtra("name") ?: ""

        if (nickname!=""){
            binding.enterName.setText(nickname)
        }

        val userDb = UserRoomDatabase.getInstance(application)
        val userDao = userDb.userDao()

        lifecycleScope.launch {
            list = withContext(Dispatchers.IO) {
                userDao.getUsers()
            }
        }

        binding.enter.setOnClickListener {
            try {
                val name: String = binding.enterName.getText().trim().toString()

                if (name.isEmpty() || name.length > 50) {
                    throw NullPointerException()
                }
                val hashList = list.map{it.name to it}.toMap()
                if (name in hashList.keys){
                    throw IllegalArgumentException()
                }

                val intend = Intent(this, ListActivity::class.java)
                intend.putExtra("name", name)
                startActivity(intend)
                finish()
            } catch (e: NullPointerException) {
                Toast.makeText(
                    this, "Введите уникальный никнейм не более 50 символов",
                    Toast.LENGTH_SHORT
                ).show()
            }catch (e: IllegalArgumentException){
                Toast.makeText(
                    this, "Такой никнейм уже занят",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
