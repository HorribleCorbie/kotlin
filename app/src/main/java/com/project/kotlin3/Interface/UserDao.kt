package com.project.kotlin3.Interface

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.project.kotlin3.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getUsers(): List<User>

    @Delete
    fun deleteUser(user: User)

    @Delete
    fun deleteUsers(user: List<User>)

    @Insert
    fun addUser(user: User)

    @Insert
    fun addUsers(user: List<User>)

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM users where isLiked==1")
    fun getLikedUsers(): List<User>
}