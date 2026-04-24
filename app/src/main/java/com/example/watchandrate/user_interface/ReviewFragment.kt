package com.example.watchandrate.user_interface

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchandrate.R
import com.example.watchandrate.data.AppDatabase
import com.example.watchandrate.repository.ReviewRepository
import com.example.watchandrate.viewmodel.ReviewViewModel

class ReviewFragment : Fragment(R.layout.fragment_review) {

    private lateinit var viewModel: ReviewViewModel
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reviewDao = AppDatabase.getInstance(requireContext()).reviewDao()
        val repository = ReviewRepository(reviewDao)
        val tvEmptyState = view.findViewById<TextView>(R.id.tvEmptyState)
        val recyclerReviews = view.findViewById<RecyclerView>(R.id.recyclerReviews)
        val btnAddReview = view.findViewById<Button>(R.id.btnAddReview)

        viewModel = ViewModelProvider(
            this,
            ReviewViewModel.provideFactory(repository)
        )[ReviewViewModel::class.java]

        reviewAdapter = ReviewAdapter(
            onEditClick = { review ->
                val fragment = EditReviewFragment()
                fragment.arguments = bundleOf("reviewId" to review.id)

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { review ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Review")
                    .setMessage("Are you sure you want to delete this review?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteReview(review)
                        Toast.makeText(requireContext(), "Review deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        recyclerReviews.adapter = reviewAdapter
        recyclerReviews.layoutManager = LinearLayoutManager(requireContext())

        viewModel.allReviews.observe(viewLifecycleOwner) { reviews ->
            reviewAdapter.submitList(reviews)

            if (reviews.isEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                recyclerReviews.visibility = View.GONE
            } else {
                tvEmptyState.visibility = View.GONE
                recyclerReviews.visibility = View.VISIBLE
            }
        }

        btnAddReview.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AddReviewFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}