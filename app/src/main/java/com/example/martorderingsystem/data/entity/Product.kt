package com.example.martorderingsystem.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val productId: Int = 0,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int
)
