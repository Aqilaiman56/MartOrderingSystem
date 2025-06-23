// FILE: AppDatabase.kt
package com.example.martorderingsystem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mobile_mart" // must match your asset filename
                )
                    .createFromAsset("mobile_mart.db") // 👈 this copies your prebuilt DB
                    .fallbackToDestructiveMigration()     // optional: resets DB if version mismatch
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
