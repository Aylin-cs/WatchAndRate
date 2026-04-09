package com.example.watchandrate.user_interface

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.watchandrate.R
import com.example.watchandrate.model.Review

class ReviewAdapter(
    private val onEditClick: (Review) -> Unit,
    private val onDeleteClick: (Review) -> Unit
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private var reviews: List<Review> = emptyList()

    fun submitList(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position], onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int = reviews.size

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMovieTitle: TextView = itemView.findViewById(R.id.tvMovieTitle)
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvReviewText: TextView = itemView.findViewById(R.id.tvReviewText)
        private val btnEditReview: ImageButton = itemView.findViewById(R.id.btnEditReview)
        private val btnDeleteReview: ImageButton = itemView.findViewById(R.id.btnDeleteReview)

        fun bind(
            review: Review,
            onEditClick: (Review) -> Unit,
            onDeleteClick: (Review) -> Unit
        ) {
            tvMovieTitle.text = review.movieTitle
            tvUsername.text = review.username
            tvReviewText.text = review.text

            btnEditReview.setOnClickListener {
                onEditClick(review)
            }

            btnDeleteReview.setOnClickListener {
                onDeleteClick(review)
            }
        }
    }
}