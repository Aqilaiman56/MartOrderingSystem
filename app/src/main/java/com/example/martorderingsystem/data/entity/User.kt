// FILE: data/User.kt
package com.example.martorderingsystem.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val userID: Int,
    val username: String,
    val email: String,
    val password: String
)

