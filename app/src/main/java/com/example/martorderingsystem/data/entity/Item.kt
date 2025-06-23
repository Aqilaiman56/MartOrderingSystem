package com.example.martorderingsystem.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Items")
data class Item(
    @PrimaryKey val itemID: Int,
    val itemName: String,
    val price: Double,
    val description: String?, // Optional
    val image: String?        // Optional
)

