package com.example.martorderingsystem.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "feedback",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Feedback(
    @PrimaryKey(autoGenerate = true) val feedbackId: Int = 0,
    val userId: Int,
    val message: String,
    val rating: Int
)
