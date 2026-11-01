package com.project.kotlin3

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.project.kotlin3.Interface.UserDao
import com.project.kotlin3.Interface.TypeDao

@Database(
    entities = [
        User::class,
        TypeUserClass::class
    ],
    version = 2
)
@TypeConverters(TypeConverter::class)
abstract class UserRoomDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun typeDao(): TypeDao

    companion object {
        private var INSTANCE: UserRoomDatabase? = null

        private val callback = object : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                fillDatabase(db)
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                fillDatabase(db)
            }

            private fun fillDatabase(db: SupportSQLiteDatabase) {

                db.execSQL(
                    "INSERT INTO `type` (id, type) VALUES (1, 'PC')"
                )
                db.execSQL(
                    "INSERT INTO `type` (id, type) VALUES (2, 'Phone')"
                )
                db.execSQL(
                    "INSERT INTO `type` (id, type) VALUES (3, 'Tablet')"
                )

                db.execSQL(
                    "INSERT INTO users (id, name, type, isLiked) VALUES (1, 'Computer', 1, 0)"
                )
                db.execSQL(
                    "INSERT INTO users (id, name, type, isLiked) VALUES (2, 'Samsung', 2, 0)"
                )
                db.execSQL(
                    "INSERT INTO users (id, name, type, isLiked) VALUES (3, 'iPad', 3, 1)"
                )
                db.execSQL(
                    "INSERT INTO users (id, name, type, isLiked) VALUES (4, 'Xiaomi', 2, 0)"
                )
            }
        }

        fun getInstance(context: Context): UserRoomDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserRoomDatabase::class.java,
                        "User_database"
                    )
                        .addCallback(callback)
                        .fallbackToDestructiveMigration(true)
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}