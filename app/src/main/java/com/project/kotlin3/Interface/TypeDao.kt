package com.project.kotlin3.Interface

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.project.kotlin3.TypeUserClass

@Dao
interface TypeDao {
    @Query("SELECT * FROM type")
    fun getTypes(): List<TypeUserClass>

    @Delete
    fun deleteType(type: TypeUserClass)

    @Insert
    fun addType(type: TypeUserClass)

    @Update
    fun updateType(type: TypeUserClass)
}