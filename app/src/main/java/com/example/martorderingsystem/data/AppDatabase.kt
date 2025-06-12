package com.example.martorderingsystem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.martorderingsystem.data.dao.*
import com.example.martorderingsystem.data.entity.*

@Database(
    entities = [User::class, Product::class, Order::class, OrderDetail::class, Feedback::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun orderDetailDao(): OrderDetailDao
    abstract fun feedbackDao(): FeedbackDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mart_ordering_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
