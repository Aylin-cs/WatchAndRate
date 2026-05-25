package com.example.watchandrate.repository

import com.example.watchandrate.data.CommentDao
import com.example.watchandrate.model.Comment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class CommentRepository(
    private val commentDao: CommentDao
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val commentsCollection = firestore.collection("comments")

    fun getCommentsForReview(reviewId: String): Flow<List<Comment>> {
        return commentDao.getCommentsForReview(reviewId)
    }

    suspend fun insertComment(comment: Comment) {
        commentDao.insertComment(comment)

        try {
            commentsCollection.document(comment.id)
                .set(comment)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun syncComments(reviewId: String) {
        try {
            val snapshot = commentsCollection
                .whereEqualTo("reviewId", reviewId)
                .get()
                .await()

            val comments = snapshot.toObjects(Comment::class.java)

            comments.forEach {
                commentDao.insertComment(it)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}