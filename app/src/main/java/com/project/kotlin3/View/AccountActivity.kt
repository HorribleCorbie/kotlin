package com.project.kotlin3.View

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import com.project.kotlin3.R
import com.project.kotlin3.TypeUser
import com.project.kotlin3.User
import com.project.kotlin3.UserRepository.currentUser
import com.project.kotlin3.databinding.ActivityAccountBinding

class AccountActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.enterName.setText(currentUser?.name)

        lateinit var type: TypeUser
        val typeUser = TypeUser.entries.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typeUser)
        binding.type.adapter = adapter
        val position: Int = adapter.getPosition(currentUser?.type)

        binding.type.setSelection(position)

        binding.type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                type = p0?.getItemAtPosition(p2) as TypeUser
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                binding.type.setSelection(0)
            }
        }

        binding.save.setOnClickListener {
            try {
                val name = binding.enterName.getText().trim().toString()
                val list = ListActivity.list

                if (name.isEmpty() || name.length > 50) {
                    throw NullPointerException()
                }
                if (name in list.keys) {
                    throw IllegalArgumentException()
                }

                currentUser?.change(name, type)

                val intend = Intent(this, ListActivity::class.java)
                startActivity(intend)
                finish()

            } catch (e: IllegalArgumentException) {
                Toast.makeText(
                    this, "Такой никнейм уже занят",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: NullPointerException) {
                Toast.makeText(
                    this, "Введите уникальный никнейм не более 50 символов",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.bottomNavigation.menu[2].isChecked = true

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
                    val intend = Intent(this, ListActivity::class.java)
                    startActivity(intend)
                    finish()
                    true
                }

                R.id.account -> {
                    true
                }

                else -> {
                    false
                }
            }
        }

    }
}