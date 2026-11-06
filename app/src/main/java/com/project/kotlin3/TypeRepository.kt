package com.project.kotlin3

import com.project.kotlin3.Interface.TypeDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object TypeRepository {
    var typeDao: TypeDao? = null
    private var types: List<TypeUserClass>  = emptyList()

    fun init(D: TypeDao) {
        typeDao = D
        types = typeDao!!.getTypes()
    }

    fun returnType(id: Int): TypeUser?{
        val type = types.find { it.id==id }
        return type?.type
    }

    fun returnId(type: TypeUser): Int?{
        val id = types.find { it.type==type }
        return id?.id
    }
}