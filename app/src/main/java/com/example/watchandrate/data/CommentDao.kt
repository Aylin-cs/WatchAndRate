package com.example.watchandrate.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.watchandrate.model.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)

    @Query("SELECT * FROM comments WHERE reviewId = :reviewId ORDER BY createdAt ASC")
    fun getCommentsForReview(reviewId: String): Flow<List<Comment>>
}