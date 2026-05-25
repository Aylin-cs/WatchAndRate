package com.example.watchandrate.user_interface

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.watchandrate.R
import com.example.watchandrate.model.Review

class ReviewAdapter(
    private val currentUserId: String?,
    private val onEditClick: (Review) -> Unit,
    private val onCommentsClick: (Review) -> Unit,
    private val onDeleteClick: (Review) -> Unit
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private var reviews: List<Review> = emptyList()
    private var commentCounts: Map<String, Int> = emptyMap()
    private val likedReviewIds = mutableSetOf<String>()

    fun submitList(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }

    fun updateCommentCounts(newCounts: Map<String, Int>) {
        commentCounts = newCounts
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
            currentUserId = currentUserId,
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
            },
            onCommentsClick = onCommentsClick,
            commentCount = commentCounts[reviews[position].id] ?: 0
        )
    }

    override fun getItemCount(): Int = reviews.size

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMovieTitle: TextView = itemView.findViewById(R.id.tvMovieTitle)
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvReviewText: TextView = itemView.findViewById(R.id.tvReviewText)
        private val ivReviewImage: ImageView = itemView.findViewById(R.id.ivReviewImage)

        private val btnEditReview: ImageButton = itemView.findViewById(R.id.btnEditReview)
        private val btnDeleteReview: ImageButton = itemView.findViewById(R.id.btnDeleteReview)
        private val btnLikeReview: Button = itemView.findViewById(R.id.btnLikeReview)
        private val btnOpenReview: Button = itemView.findViewById(R.id.btnOpenReview)
        private val btnCommentsReview: Button = itemView.findViewById(R.id.btnCommentsReview)

        private val tvStars: TextView = itemView.findViewById(R.id.tvStars)

        fun bind(
            review: Review,
            currentUserId: String?,
            isLiked: Boolean,
            onEditClick: (Review) -> Unit,
            onDeleteClick: (Review) -> Unit,
            onLikeClick: (Review) -> Unit,
            onCommentsClick: (Review) -> Unit,
            commentCount: Int
        ) {
            tvMovieTitle.text = review.movieTitle
            tvUsername.text = review.username
            tvReviewText.text = review.text
            tvStars.text = "★★★★★"

            if (!review.imageUrl.isNullOrEmpty()) {
                try {
                    val bytes = Base64.decode(review.imageUrl, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    ivReviewImage.setImageBitmap(bitmap)
                    ivReviewImage.visibility = View.VISIBLE
                } catch (e: Exception) {
                    ivReviewImage.visibility = View.GONE
                }
            } else {
                ivReviewImage.visibility = View.GONE
            }

            val isOwner = review.userId == currentUserId

            btnEditReview.visibility = if (isOwner) View.VISIBLE else View.GONE
            btnDeleteReview.visibility = if (isOwner) View.VISIBLE else View.GONE

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
                val message =
                    "🎬 ${review.movieTitle}\n\n" +
                            "👤 Reviewed by: ${review.username}\n\n" +
                            "⭐ Rating: ★★★★★\n\n" +
                            "💬 ${review.text}"

                androidx.appcompat.app.AlertDialog.Builder(itemView.context)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Review Details")
                    .setMessage(message)
                    .setPositiveButton("Close", null)
                    .setCancelable(true)
                    .show()
            }

            btnCommentsReview.text = "$commentCount comments"

            btnCommentsReview.setOnClickListener {
                onCommentsClick(review)
            }
        }
    }
}