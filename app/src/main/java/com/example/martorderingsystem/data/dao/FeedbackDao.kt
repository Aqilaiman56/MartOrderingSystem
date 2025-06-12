package com.example.martorderingsystem.data.dao

import androidx.room.*
import com.example.martorderingsystem.data.entity.Feedback
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: Feedback)

    @Query("SELECT * FROM feedback WHERE userId = :userId")
    fun getFeedbackByUser(userId: Int): Flow<List<Feedback>>

    @Query("SELECT * FROM feedback")
    fun getAllFeedback(): Flow<List<Feedback>>

    @Delete
    suspend fun deleteFeedback(feedback: Feedback)
}
