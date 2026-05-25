package com.example.watchandrate.user_interface

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.watchandrate.R
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_reviewFragment)
            return
        }
        val emailEditText = view.findViewById<EditText>(R.id.etLoginEmail)
        val passwordEditText = view.findViewById<EditText>(R.id.etLoginPassword)
        val loginButton = view.findViewById<Button>(R.id.btnLogin)
        val goToRegister = view.findViewById<TextView>(R.id.tvGoToRegister)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_reviewFragment)
                }
                .addOnFailureListener { error ->
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
        }

        goToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}