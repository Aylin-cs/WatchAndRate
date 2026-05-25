package com.example.watchandrate.user_interface

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.watchandrate.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var selectedImageUri: Uri? = null
    private lateinit var ivProfileImage: ImageView

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                ivProfileImage.setImageURI(uri)
            }
        }

    private fun convertImageToBase64(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        val scaledBitmap = Bitmap.createScaledBitmap(
            originalBitmap,
            300,
            (originalBitmap.height * (300.0 / originalBitmap.width)).toInt(),
            true
        )

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun showBase64Image(base64: String) {
        try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ivProfileImage.setImageBitmap(bitmap)
        } catch (_: Exception) {
            ivProfileImage.setImageResource(android.R.drawable.ic_menu_camera)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvStatus = view.findViewById<TextView>(R.id.tvProfileStatus)
        val btnSaveProfile = view.findViewById<Button>(R.id.btnSaveProfile)
        val btnBackToReviews = view.findViewById<Button>(R.id.btnBackToReviews)
        val btnSelectProfileImage = view.findViewById<Button>(R.id.btnSelectProfileImage)
        val btnDeleteProfileImage = view.findViewById<Button>(R.id.btnDeleteProfileImage)
        ivProfileImage = view.findViewById(R.id.ivProfileImage)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val firestore = FirebaseFirestore.getInstance()

        etUsername.setText(
            currentUser?.displayName
                ?: currentUser?.email?.substringBefore("@")
                ?: ""
        )

        tvEmail.text = currentUser?.email ?: "No email found"

        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val imageBase64 = document.getString("profileImage")
                    if (!imageBase64.isNullOrEmpty()) {
                        showBase64Image(imageBase64)
                    }
                }
        }

        btnSelectProfileImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        btnDeleteProfileImage.setOnClickListener {
            selectedImageUri = null
            ivProfileImage.setImageResource(android.R.drawable.ic_menu_camera)

            currentUser?.let { user ->
                firestore.collection("users").document(user.uid)
                    .update("profileImage", "")
                    .addOnSuccessListener {
                        tvStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                        tvStatus.text = "Photo deleted"
                    }
                    .addOnFailureListener {
                        tvStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                        tvStatus.text = "Failed to delete photo"
                    }
            }
        }

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
                .addOnSuccessListener {
                    val userData = mutableMapOf<String, Any>(
                        "userId" to currentUser.uid,
                        "username" to newName,
                        "email" to (currentUser.email ?: "")
                    )

                    selectedImageUri?.let { uri ->
                        userData["profileImage"] = convertImageToBase64(uri)
                    }

                    firestore.collection("users").document(currentUser.uid)
                        .set(userData, com.google.firebase.firestore.SetOptions.merge())
                        .addOnSuccessListener {
                            btnSaveProfile.isEnabled = true
                            tvStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                            tvStatus.text = "Profile saved successfully"
                        }
                        .addOnFailureListener {
                            btnSaveProfile.isEnabled = true
                            tvStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                            tvStatus.text = "Failed to save profile"
                        }
                }
                .addOnFailureListener {
                    btnSaveProfile.isEnabled = true
                    tvStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                    tvStatus.text = "Failed to save profile"
                }
        }

        btnBackToReviews.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}