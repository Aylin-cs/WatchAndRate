package com.example.watchandrate.user_interface

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.watchandrate.R
import com.example.watchandrate.data.AppDatabase
import com.example.watchandrate.model.Review
import com.example.watchandrate.repository.ReviewRepository
import com.example.watchandrate.viewmodel.ReviewViewModel
import java.util.UUID
import androidx.appcompat.widget.Toolbar

class AddReviewFragment : Fragment(R.layout.fragment_add_review) {

    private lateinit var viewModel: ReviewViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reviewDao = AppDatabase.getInstance(requireContext()).reviewDao()
        val repository = ReviewRepository(reviewDao)

        viewModel = ViewModelProvider(
            this,
            ReviewViewModel.provideFactory(repository)
        )[ReviewViewModel::class.java]

        val etMovieTitle = view.findViewById<EditText>(R.id.etMovieTitle)
        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val etReviewText = view.findViewById<EditText>(R.id.etReviewText)
        val btnSaveReview = view.findViewById<Button>(R.id.btnSaveReview)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbarAddReview)

        toolbar.navigationIcon?.setTint(resources.getColor(android.R.color.white))

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSaveReview.setOnClickListener {
            val movieTitle = etMovieTitle.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val reviewText = etReviewText.text.toString().trim()

            if (movieTitle.isEmpty() || username.isEmpty() || reviewText.isEmpty()) {
                return@setOnClickListener
            }

            val review = Review(
                id = UUID.randomUUID().toString(),
                userId = username.lowercase(),
                movieId = UUID.randomUUID().toString(),
                movieTitle = movieTitle,
                text = reviewText,
                imageUrl = "",
                localImagePath = null,
                userPhotoUrl = "",
                username = username,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            viewModel.insertReview(review)

            parentFragmentManager.popBackStack()
        }
    }
}