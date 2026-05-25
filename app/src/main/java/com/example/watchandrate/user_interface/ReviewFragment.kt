package com.example.watchandrate.user_interface

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
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
import com.example.watchandrate.model.Review
import com.example.watchandrate.repository.ReviewRepository
import com.example.watchandrate.viewmodel.ReviewViewModel
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavOptions
import androidx.lifecycle.lifecycleScope
import com.example.watchandrate.network.RetrofitClient
import kotlinx.coroutines.launch
import com.example.watchandrate.data.CommentDao
import com.example.watchandrate.repository.CommentRepository
import kotlinx.coroutines.flow.first
class ReviewFragment : Fragment(R.layout.fragment_review) {

    private lateinit var viewModel: ReviewViewModel
    private lateinit var reviewAdapter: ReviewAdapter

    private var allReviews: List<Review> = emptyList()
    private var searchQuery: String = ""
    private var showOnlyMyReviews: Boolean = false

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reviewDao = AppDatabase.getInstance(requireContext()).reviewDao()
        val repository = ReviewRepository(reviewDao)
        val commentDao = AppDatabase.getInstance(requireContext()).commentDao()
        val commentRepository = CommentRepository(commentDao)
        val tvEmptyState = view.findViewById<TextView>(R.id.tvEmptyState)
        val recyclerReviews = view.findViewById<RecyclerView>(R.id.recyclerReviews)
        val btnAddReview = view.findViewById<Button>(R.id.btnAddReview)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val btnMyReviews = view.findViewById<Button>(R.id.btnMyReviews)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val btnProfile = view.findViewById<Button>(R.id.btnProfile)
        val btnSearchMovieApi = view.findViewById<Button>(R.id.btnSearchMovieApi)

        viewModel = ViewModelProvider(
            this,
            ReviewViewModel.provideFactory(repository)
        )[ReviewViewModel::class.java]
        viewModel.syncReviewsFromFirestore()

        reviewAdapter = ReviewAdapter(
            currentUserId = currentUserId,
            onEditClick = { review ->
                findNavController().navigate(
                    R.id.action_reviewFragment_to_editReviewFragment,
                    bundleOf("reviewId" to review.id)
                )
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
            },
            onCommentsClick = { review ->
                findNavController().navigate(
                    R.id.action_reviewFragment_to_commentsFragment,
                    bundleOf("reviewId" to review.id)
                )
            },

            onLikeClick = { review ->
                val userId = currentUserId ?: return@ReviewAdapter

                val updatedLikedBy = if (review.likedBy.contains(userId)) {
                    review.likedBy - userId
                } else {
                    review.likedBy + userId
                }

                val updatedReview = review.copy(
                    likedBy = updatedLikedBy,
                    likesCount = updatedLikedBy.size,
                    updatedAt = System.currentTimeMillis()
                )

                viewModel.updateReview(updatedReview)
            }
        )

        recyclerReviews.adapter = reviewAdapter
        recyclerReviews.layoutManager = LinearLayoutManager(requireContext())

        viewModel.allReviews.observe(viewLifecycleOwner) { reviews ->
            allReviews = reviews
            lifecycleScope.launch {
                val counts = mutableMapOf<String, Int>()

                reviews.forEach { review ->
                    val commentCount =
                        commentRepository.getCommentsForReview(review.id)
                            .first()
                            .size

                    counts[review.id] = commentCount
                }

                reviewAdapter.updateCommentCounts(counts)
            }
            updateReviewList(tvEmptyState, recyclerReviews)
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString()
                updateReviewList(tvEmptyState, recyclerReviews)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnMyReviews.setOnClickListener {
            showOnlyMyReviews = !showOnlyMyReviews

            btnMyReviews.text = if (showOnlyMyReviews) {
                "All reviews"
            } else {
                "My reviews"
            }

            updateReviewList(tvEmptyState, recyclerReviews)
        }

        btnAddReview.setOnClickListener {
            findNavController().navigate(R.id.action_reviewFragment_to_addReviewFragment)
        }

        btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_reviewFragment_to_profileFragment)
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            findNavController().navigate(
                R.id.loginFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build()
            )
        }
        btnSearchMovieApi.setOnClickListener {
            val movieTitle = etSearch.text.toString().trim()

            if (movieTitle.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a movie name in search", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val movie = RetrofitClient.omdbApi.searchMovie(
                        apiKey = "2c8b3ac4",
                        title = movieTitle
                    )

                    if (movie.Response == "True") {
                        AlertDialog.Builder(requireContext())
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("${movie.Title} (${movie.Year})")
                            .setMessage(
                                "Genre: ${movie.Genre}\n" +
                                        "Director: ${movie.Director}\n" +
                                        "Rating: ${movie.imdbRating}\n\n" +
                                        "${movie.Plot}"
                            )
                            .setPositiveButton("OK", null)
                            .setCancelable(true)
                            .show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            movie.Error ?: "Movie not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "API error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun updateReviewList(
        tvEmptyState: TextView,
        recyclerReviews: RecyclerView
    ) {
        val filteredReviews = allReviews.filter { review ->
            val matchesSearch =
                review.movieTitle.contains(searchQuery, ignoreCase = true) ||
                        review.text.contains(searchQuery, ignoreCase = true) ||
                        review.username.contains(searchQuery, ignoreCase = true)

            val matchesUser =
                !showOnlyMyReviews ||
                        review.userId == currentUserId

            matchesSearch && matchesUser
        }

        reviewAdapter.submitList(filteredReviews)

        if (filteredReviews.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            recyclerReviews.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            recyclerReviews.visibility = View.VISIBLE
        }
    }
}