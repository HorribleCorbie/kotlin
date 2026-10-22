package com.project.kotlin3.View

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.project.kotlin3.R
import com.project.kotlin3.TypeUser
import com.project.kotlin3.User
import com.project.kotlin3.UserRepository
import com.project.kotlin3.UserRepository.currentUser
import com.project.kotlin3.UserRepository.selectedUser
import com.project.kotlin3.databinding.ActivityAccountBinding

class AccountActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)

        val intent = getIntent()
        val user = intent.getStringExtra("user") ?: ""
        val index = intent.getIntExtra("index", -1)

        setContentView(binding.root)

        var currentName: String?
        var currentType: TypeUser?

        if (user == "currentUser"){
            currentName = currentUser?.name
            currentType = currentUser?.type
        }
        else{
            currentName = selectedUser?.name ?: ""
            currentType = selectedUser?.type ?: TypeUser.Phone
        }
        binding.enterName.setText(currentName)

        lateinit var type: TypeUser
        val typeUser = TypeUser.entries.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typeUser)
        binding.type.adapter = adapter
        val position: Int = adapter.getPosition(currentType)

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
                val list = UserRepository.getUsers()

                if (name.isEmpty() || name.length > 50) {
                    throw NullPointerException()
                }
                val hashList = list.map{it.name to it}.toMap()
                if (name in hashList.keys && currentName!=name || name==currentUser?.name){
                    throw IllegalArgumentException()
                }

                if (user == "currentUser"){
                    currentUser?.change(name, type)
                }
                else if(selectedUser!=null && index==-1){
                    val user = selectedUser
                    selectedUser?.change(name, type)
                    UserRepository.changeUser(user!!)
                }else{
                    selectedUser=User(name, type, false)
                    UserRepository.createUser(index)
                }

                Toast.makeText(
                    this, "Сохранено",
                    Toast.LENGTH_SHORT
                ).show()
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