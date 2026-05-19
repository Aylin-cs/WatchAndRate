package com.example.watchandrate.auth

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object AuthManager {

    private const val TAG = "AuthManager"

    private lateinit var auth: FirebaseAuth

    fun init(context: Context) {
        FirebaseApp.initializeApp(context)
        auth = FirebaseAuth.getInstance()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        onComplete: (Boolean, String?) -> Unit
    ) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    Log.d(TAG, "signInWithEmailAndPassword:success")
                    onComplete(true, null)

                } else {

                    Log.w(TAG, "signInWithEmailAndPassword:failure", task.exception)
                    onComplete(false, task.exception?.message)
                }
            }
    }

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        onComplete: (Boolean, String?, String?) -> Unit
    ) {

        withContext(Dispatchers.IO) {

            try {

                val result = auth.createUserWithEmailAndPassword(email, password).await()

                val userId = result.user?.uid

                onComplete(true, null, userId)

            } catch (e: Exception) {

                val errorMessage = when (e) {

                    is FirebaseAuthInvalidCredentialsException ->
                        "Invalid email address."

                    is FirebaseAuthUserCollisionException ->
                        "Email address is already registered."

                    else -> e.message
                }

                onComplete(false, errorMessage, null)
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }
}