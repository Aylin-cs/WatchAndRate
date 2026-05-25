package com.example.watchandrate.user_interface

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.watchandrate.R
import com.example.watchandrate.data.AppDatabase
import com.example.watchandrate.model.Comment
import com.example.watchandrate.repository.CommentRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class CommentsFragment : Fragment(R.layout.fragment_comments) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reviewId = arguments?.getString("reviewId") ?: return

        val toolbar = view.findViewById<Toolbar>(R.id.toolbarComments)
        val tvComments = view.findViewById<TextView>(R.id.tvComments)
        val etComment = view.findViewById<EditText>(R.id.etComment)
        val btnAddComment = view.findViewById<Button>(R.id.btnAddComment)
        val btnBack = view.findViewById<Button>(R.id.btnBackFromComments)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        val commentDao = AppDatabase.getInstance(requireContext()).commentDao()
        val repository = CommentRepository(commentDao)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        lifecycleScope.launch {
            repository.syncComments(reviewId)

            repository.getCommentsForReview(reviewId)
                .collectLatest { comments ->

                    tvComments.text = comments.joinToString("\n\n") {
                        "${it.username}: ${it.text}"
                    }
                }
        }

        btnAddComment.setOnClickListener {

            val text = etComment.text.toString().trim()

            if (text.isEmpty()) return@setOnClickListener

            val currentUser = FirebaseAuth.getInstance().currentUser
                ?: return@setOnClickListener

            val comment = Comment(
                id = UUID.randomUUID().toString(),
                reviewId = reviewId,
                userId = currentUser.uid,
                username = currentUser.email ?: "User",
                text = text,
                createdAt = System.currentTimeMillis()
            )

            lifecycleScope.launch {
                repository.insertComment(comment)
            }

            etComment.text.clear()
        }
    }
}