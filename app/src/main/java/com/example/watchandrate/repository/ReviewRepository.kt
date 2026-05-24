package com.example.watchandrate.repository

import com.example.watchandrate.data.ReviewDao
import com.example.watchandrate.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class ReviewRepository(private val reviewDao: ReviewDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val reviewsCollection = firestore.collection("reviews")

    fun getAllReviews(): Flow<List<Review>> {
        return reviewDao.getAllReviews()
    }

    fun getUserReviews(userId: String): Flow<List<Review>> {
        return reviewDao.getUserReviews(userId)
    }

    suspend fun getReviewById(reviewId: String): Review? {
        return reviewDao.getReviewById(reviewId)
    }

    // 🔥 חשוב – קודם שומרים ל־Room, ואז מנסים Firebase
    suspend fun insertReview(review: Review) {
        reviewDao.insertReview(review)

        try {
            reviewsCollection.document(review.id).set(review).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateReview(review: Review) {
        reviewDao.updateReview(review)

        try {
            reviewsCollection.document(review.id).set(review).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteReview(review: Review) {
        reviewDao.deleteReview(review)

        try {
            reviewsCollection.document(review.id).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}