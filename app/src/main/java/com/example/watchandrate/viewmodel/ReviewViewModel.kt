package com.example.watchandrate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.watchandrate.model.Review
import com.example.watchandrate.repository.ReviewRepository
import kotlinx.coroutines.launch

class ReviewViewModel(private val reviewRepository: ReviewRepository) : ViewModel() {

    val allReviews: LiveData<List<Review>> =
        reviewRepository.getAllReviews().asLiveData()

    fun syncReviewsFromFirestore() {
        viewModelScope.launch {
            reviewRepository.syncReviewsFromFirestore()
        }
    }

    fun getUserReviews(userId: String): LiveData<List<Review>> {
        return reviewRepository.getUserReviews(userId).asLiveData()
    }

    fun getReviewById(reviewId: String): LiveData<Review?> {
        val reviewLiveData = MutableLiveData<Review?>()
        viewModelScope.launch {
            reviewLiveData.value = reviewRepository.getReviewById(reviewId)
        }
        return reviewLiveData
    }

    fun insertReview(review: Review, onFinished: () -> Unit = {}) {
        viewModelScope.launch {
            reviewRepository.insertReview(review)
            onFinished()
        }
    }

    fun updateReview(review: Review) {
        viewModelScope.launch {
            reviewRepository.updateReview(review)
        }
    }

    fun deleteReview(review: Review) {
        viewModelScope.launch {
            reviewRepository.deleteReview(review)
        }
    }

    companion object {
        fun provideFactory(repository: ReviewRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ReviewViewModel(repository) as T
                }
            }
    }
}