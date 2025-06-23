// FILE: data/UserDao.kt
package com.example.martorderingsystem.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Entity
import androidx.room.PrimaryKey

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun getUser(username: String, password: String): User?
}
