package com.example.watchandrate.user_interface

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.watchandrate.R

class ReviewFragment : Fragment(R.layout.fragment_review) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvMovieTitle = view.findViewById<TextView>(R.id.tvMovieTitle)
        val tvUsername = view.findViewById<TextView>(R.id.tvUsername)
        val tvReviewText = view.findViewById<TextView>(R.id.tvReviewText)

        tvMovieTitle.text = "The Matrix"
        tvUsername.text = "Aylin"
        tvReviewText.text = "This is my first review screen."
    }
}