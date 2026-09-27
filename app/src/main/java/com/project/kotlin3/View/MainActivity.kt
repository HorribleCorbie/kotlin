package com.project.kotlin3.View

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.project.kotlin3.databinding.ActivityMainBinding

//3.  Bluetooth-чат на базе Android
//Пользователь вводит имя, которые будет отображаться в чате и получает список найденных устройств.
//I2 – CheckBox
//L1 – Linear
//O4 – ScrollView

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(getLayoutInflater())
        setContentView(binding.getRoot())

        binding.enter.setOnClickListener {
            try {
                val name: String = binding.enterName.getText().trim().toString()
                val list = ListActivity.list

                if (name.isEmpty() || name.length > 50) {
                    throw NullPointerException()
                }
                if (name in list.keys){
                    throw IllegalArgumentException()
                }

                val intend = Intent(this, ListActivity::class.java)
                intend.putExtra("name", name)
                startActivity(intend)
                finish()
            } catch (e: NullPointerException) {
                Toast.makeText(
                    this@MainActivity, "Введите уникальный никнейм не более 50 символов",
                    Toast.LENGTH_SHORT
                ).show()
            }catch (e: IllegalArgumentException){
                Toast.makeText(
                    this@MainActivity, "Такой никнейм уже занят",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}