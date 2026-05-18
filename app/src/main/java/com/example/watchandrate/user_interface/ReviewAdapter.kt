package com.example.watchandrate.user_interface

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    private val likedReviewIds = mutableSetOf<String>()

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
        holder.bind(
            review = reviews[position],
            isLiked = likedReviewIds.contains(reviews[position].id),
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
            onLikeClick = { review ->
                if (likedReviewIds.contains(review.id)) {
                    likedReviewIds.remove(review.id)
                } else {
                    likedReviewIds.add(review.id)
                }

                notifyItemChanged(position)
            }
        )
    }

    override fun getItemCount(): Int = reviews.size

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMovieTitle: TextView = itemView.findViewById(R.id.tvMovieTitle)
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvReviewText: TextView = itemView.findViewById(R.id.tvReviewText)

        private val btnEditReview: ImageButton = itemView.findViewById(R.id.btnEditReview)
        private val btnDeleteReview: ImageButton = itemView.findViewById(R.id.btnDeleteReview)
        private val btnLikeReview: Button = itemView.findViewById(R.id.btnLikeReview)
        private val btnOpenReview: Button = itemView.findViewById(R.id.btnOpenReview)
        private val btnCommentsReview: Button = itemView.findViewById(R.id.btnCommentsReview)

        fun bind(
            review: Review,
            isLiked: Boolean,
            onEditClick: (Review) -> Unit,
            onDeleteClick: (Review) -> Unit,
            onLikeClick: (Review) -> Unit
        ) {
            tvMovieTitle.text = review.movieTitle
            tvUsername.text = review.username
            tvReviewText.text = review.text

            btnLikeReview.text = if (isLiked) {
                "♥ Like · 1"
            } else {
                "♡ Like · 0"
            }

            btnEditReview.setOnClickListener {
                onEditClick(review)
            }

            btnDeleteReview.setOnClickListener {
                onDeleteClick(review)
            }

            btnLikeReview.setOnClickListener {
                onLikeClick(review)
            }

            btnOpenReview.setOnClickListener {
                // TODO: open review details screen
            }

            btnCommentsReview.setOnClickListener {
                // TODO: open comments screen
            }
        }
    }
}