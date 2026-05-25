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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.watchandrate.R
import com.example.watchandrate.data.AppDatabase
import com.example.watchandrate.model.Review
import com.example.watchandrate.repository.ReviewRepository
import com.example.watchandrate.viewmodel.ReviewViewModel
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddReviewFragment : Fragment(R.layout.fragment_add_review) {

    private lateinit var viewModel: ReviewViewModel
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
            view?.findViewById<ImageView>(R.id.ivSelectedImage)?.apply {
                setImageURI(uri)
                visibility = View.VISIBLE
            }
        }

    private fun convertImageToBase64(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        val scaledBitmap = Bitmap.createScaledBitmap(
            originalBitmap,
            400,
            (originalBitmap.height * (400.0 / originalBitmap.width)).toInt(),
            true
        )

        val outputStream = ByteArrayOutputStream()

        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

        val bytes = outputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

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
        val btnSelectImage = view.findViewById<Button>(R.id.btnSelectImage)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbarAddReview)

        toolbar.navigationIcon?.setTint(resources.getColor(android.R.color.white))

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSelectImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        btnSaveReview.setOnClickListener {
            val movieTitle = etMovieTitle.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val reviewText = etReviewText.text.toString().trim()

            if (movieTitle.isEmpty() || username.isEmpty() || reviewText.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = FirebaseAuth.getInstance().currentUser
                ?: return@setOnClickListener

            btnSaveReview.isEnabled = false

            try {
                val reviewId = UUID.randomUUID().toString()
                val base64Image = selectedImageUri?.let { convertImageToBase64(it) } ?: ""

                val review = Review(
                    id = reviewId,
                    userId = currentUser.uid,
                    movieId = UUID.randomUUID().toString(),
                    movieTitle = movieTitle,
                    text = reviewText,
                    imageUrl = base64Image,
                    localImagePath = null,
                    userPhotoUrl = "",
                    username = username,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                viewModel.insertReview(review) {
                    parentFragmentManager.popBackStack()
                }

            } catch (e: Exception) {
                btnSaveReview.isEnabled = true
                Toast.makeText(
                    requireContext(),
                    "Image save failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}