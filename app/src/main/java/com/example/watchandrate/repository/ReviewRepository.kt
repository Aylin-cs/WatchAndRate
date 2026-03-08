package com.example.watchandrate.repository

import com.example.watchandrate.data.ReviewDao
import com.example.watchandrate.model.Review
import kotlinx.coroutines.flow.Flow

class ReviewRepository(private val reviewDao: ReviewDao) {

    fun getAllReviews(): Flow<List<Review>> {
        return reviewDao.getAllReviews()
    }

    fun getUserReviews(userId: String): Flow<List<Review>> {
        return reviewDao.getUserReviews(userId)
    }

    suspend fun getReviewById(reviewId: String): Review? {
        return reviewDao.getReviewById(reviewId)
    }

    suspend fun insertReview(review: Review) {
        reviewDao.insertReview(review)
    }

    suspend fun updateReview(review: Review) {
        reviewDao.updateReview(review)
    }

    suspend fun deleteReview(review: Review) {
        reviewDao.deleteReview(review)
    }
}