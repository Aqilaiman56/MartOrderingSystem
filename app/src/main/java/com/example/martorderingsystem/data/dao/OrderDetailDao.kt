package com.example.martorderingsystem.data.dao

import androidx.room.*
import com.example.martorderingsystem.data.entity.OrderDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderDetail(orderDetail: OrderDetail)

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    fun getOrderDetailsByOrderId(orderId: Int): Flow<List<OrderDetail>>

    @Delete
    suspend fun deleteOrderDetail(orderDetail: OrderDetail)
}
