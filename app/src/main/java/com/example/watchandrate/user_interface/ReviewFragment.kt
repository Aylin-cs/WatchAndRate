package com.example.watchandrate.user_interface

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchandrate.R
import com.example.watchandrate.data.AppDatabase
import com.example.watchandrate.model.Review
import com.example.watchandrate.repository.ReviewRepository
import com.example.watchandrate.viewmodel.ReviewViewModel
import java.util.UUID

class ReviewFragment : Fragment(R.layout.fragment_review) {

    private lateinit var viewModel: ReviewViewModel
    private lateinit var reviewAdapter: ReviewAdapter
    private var sampleInserted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reviewDao = AppDatabase.getInstance(requireContext()).reviewDao()
        val repository = ReviewRepository(reviewDao)

        viewModel = ViewModelProvider(
            this,
            ReviewViewModel.provideFactory(repository)
        )[ReviewViewModel::class.java]

        val recyclerReviews = view.findViewById<RecyclerView>(R.id.recyclerReviews)
        val btnAddReview = view.findViewById<Button>(R.id.btnAddReview)

        reviewAdapter = ReviewAdapter { review ->
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Review")
                .setMessage("Are you sure you want to delete this review?")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteReview(review)
                    Toast.makeText(requireContext(), "Review deleted", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null).show()
        }

        recyclerReviews.adapter = reviewAdapter
        recyclerReviews.layoutManager = LinearLayoutManager(requireContext())

        viewModel.allReviews.observe(viewLifecycleOwner) { reviews ->
            reviewAdapter.submitList(reviews)

            if (reviews.isEmpty() && !sampleInserted) {
                sampleInserted = true
                insertSampleReview()
            }
        }

        btnAddReview.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AddReviewFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun insertSampleReview() {
        val sampleReview = Review(
            id = UUID.randomUUID().toString(),
            userId = "user_1",
            movieId = "tt0133093",
            movieTitle = "The Matrix",
            text = "Amazing movie with a great story.",
            imageUrl = "",
            localImagePath = null,
            userPhotoUrl = "",
            username = "Eden",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        viewModel.insertReview(sampleReview)
    }
}