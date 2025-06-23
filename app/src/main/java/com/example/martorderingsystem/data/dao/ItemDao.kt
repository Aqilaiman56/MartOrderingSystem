package com.example.martorderingsystem.data.dao

import androidx.room.*
import com.example.martorderingsystem.data.entity.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM Items")
    suspend fun getAll(): List<Item>

    @Insert
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)
}

