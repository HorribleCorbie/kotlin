package com.project.kotlin3.View

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.project.kotlin3.R
import com.project.kotlin3.TypeUser
import com.project.kotlin3.User
import com.project.kotlin3.databinding.ActivityListChatsBinding
import androidx.core.graphics.toColorInt
import androidx.core.widget.TextViewCompat

class ListActivity : AppCompatActivity() {
    lateinit var binding: ActivityListChatsBinding
    var isNotSelectAll = false

    //4 лаба: вариант 1 и 2
    companion object {
        var list = mutableMapOf(
            "user1" to User("user1", TypeUser.PC),
            "user2" to User("user2", TypeUser.Tablet),
            "user3" to User("user3", TypeUser.Phone),
            "user4" to User("user4", TypeUser.PC),
            "user5" to User("user5", TypeUser.Phone),
            "user6" to User("user6", TypeUser.Tablet),
            "user7" to User("user7", TypeUser.Tablet),
            "user8" to User("user8", TypeUser.Phone),
            "user9" to User("user9", TypeUser.Phone),
            "user10" to User("user10", TypeUser.Phone),
            "user11" to User("user11", TypeUser.PC),
            "user12" to User("user12", TypeUser.PC),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val intent = getIntent()
//        val nickname = intent.getStringExtra("name") ?: "Undefined"
//        list[nickname] = User(nickname, TypeUser.Phone)

        val listCheckBox = listOf(
            binding.selectPC,
            binding.selectPhone,
            binding.selectTablet
        )

        binding.selectAll.setOnCheckedChangeListener { _, isChecked ->
            if (binding.selectAll.isChecked) {
                listCheckBox.forEach { it.isChecked = isChecked }
            } else if (!isNotSelectAll) {
                listCheckBox.forEach { it.isChecked = false }
            }
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
        binding.selectAll.isChecked =
            binding.selectTablet.isChecked && binding.selectPC.isChecked && binding.selectPhone.isChecked
        isNotSelectAll =
            binding.selectTablet.isChecked || binding.selectPC.isChecked || binding.selectPhone.isChecked
    }

    fun printChats() {
        val listCheck = HashMap<String, User>()
        binding.chats.removeAllViews()

        if (binding.selectPC.isChecked) {
            listCheck.putAll(list.filter { (key, value) -> value.type == TypeUser.PC })
        }
        if (binding.selectPhone.isChecked) {
            listCheck.putAll(list.filter { (key, value) -> value.type == TypeUser.Phone })
        }
        if (binding.selectTablet.isChecked) {
            listCheck.putAll(list.filter { (key, value) -> value.type == TypeUser.Tablet })
        }

        for (user in listCheck.values) {
            val newLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 20, 0, 20)
                }
                orientation = LinearLayout.HORIZONTAL
                setPadding(10, 15, 10, 15)
                isClickable = true
                isFocusable = true
                setBackgroundResource(R.drawable.rounded_edittext)
                backgroundTintList = ColorStateList.valueOf("#37597B".toColorInt())
                backgroundTintMode = PorterDuff.Mode.SRC_IN
            }
            val image = ImageView(this).apply {
                when (user.type) {
                    TypeUser.PC -> setImageResource(R.drawable.computer_white)
                    TypeUser.Phone -> setImageResource(R.drawable.iphone_white)
                    TypeUser.Tablet -> setImageResource(R.drawable.tablet_white)
                }
                layoutParams = LinearLayout.LayoutParams(125, 125)
            }

            val text = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    weight = 1f
                    setMargins(20, 0, 0, 0)
                }
                text = user.name
                setTextColor("#ffffff".toColorInt())
            }
            TextViewCompat.setAutoSizeTextTypeWithDefaults(
                text,
                TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
            );

            newLayout.addView(image)
            newLayout.addView(text)
            binding.chats.addView(newLayout)
        }
    }
}