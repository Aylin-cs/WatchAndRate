package com.example.watchandrate.user_interface

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.watchandrate.R
import com.example.watchandrate.data.AppDatabase
import com.example.watchandrate.model.Review
import com.example.watchandrate.repository.ReviewRepository
import com.example.watchandrate.viewmodel.ReviewViewModel

class EditReviewFragment : Fragment(R.layout.fragment_edit_review) {

    private lateinit var viewModel: ReviewViewModel
    private var currentReview: Review? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reviewDao = AppDatabase.getInstance(requireContext()).reviewDao()
        val repository = ReviewRepository(reviewDao)

        viewModel = ViewModelProvider(
            this,
            ReviewViewModel.provideFactory(repository)
        )[ReviewViewModel::class.java]

        val toolbar = view.findViewById<Toolbar>(R.id.toolbarEditReview)

        toolbar.navigationIcon?.setTint(resources.getColor(android.R.color.white))

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val etMovieTitle = view.findViewById<EditText>(R.id.etEditMovieTitle)
        val etUsername = view.findViewById<EditText>(R.id.etEditUsername)
        val etReviewText = view.findViewById<EditText>(R.id.etEditReviewText)
        val btnSaveChanges = view.findViewById<Button>(R.id.btnSaveChanges)

        val reviewId = arguments?.getString("reviewId") ?: return

        viewModel.getReviewById(reviewId).observe(viewLifecycleOwner) { review ->
            review?.let {
                currentReview = it
                etMovieTitle.setText(it.movieTitle)
                etUsername.setText(it.username)
                etReviewText.setText(it.text)
            }
        }

        btnSaveChanges.setOnClickListener {
            val existingReview = currentReview ?: return@setOnClickListener

            val updatedReview = existingReview.copy(
                movieTitle = etMovieTitle.text.toString().trim(),
                username = etUsername.text.toString().trim(),
                text = etReviewText.text.toString().trim(),
                updatedAt = System.currentTimeMillis()
            )

            viewModel.updateReview(updatedReview)

            parentFragmentManager.popBackStack()
        }
    }
}