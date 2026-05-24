package com.example.watchandrate.user_interface

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.watchandrate.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvStatus = view.findViewById<TextView>(R.id.tvProfileStatus)
        val btnSaveProfile = view.findViewById<Button>(R.id.btnSaveProfile)
        val btnBackToReviews = view.findViewById<Button>(R.id.btnBackToReviews)

        val currentUser = FirebaseAuth.getInstance().currentUser

        etUsername.setText(
            currentUser?.displayName
                ?: currentUser?.email?.substringBefore("@")
                ?: ""
        )

        tvEmail.text = currentUser?.email ?: "No email found"

        btnSaveProfile.setOnClickListener {
            val newName = etUsername.text.toString().trim()

            if (newName.isEmpty()) {
                tvStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                tvStatus.text = "Name cannot be empty"
                return@setOnClickListener
            }

            if (currentUser == null) {
                tvStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                tvStatus.text = "No logged in user"
                return@setOnClickListener
            }

            btnSaveProfile.isEnabled = false
            tvStatus.setTextColor(resources.getColor(android.R.color.darker_gray))
            tvStatus.text = "Saving..."

            val profileUpdates = userProfileChangeRequest {
                displayName = newName
            }

            currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    btnSaveProfile.isEnabled = true

                    if (task.isSuccessful) {
                        tvStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                        tvStatus.text = "Profile saved successfully"
                    } else {
                        tvStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                        tvStatus.text = "Failed to save profile"
                    }
                }
        }

        btnBackToReviews.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}