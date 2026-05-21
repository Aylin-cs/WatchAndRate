package com.example.watchandrate.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authManager = AuthManager

    private val _authState = MutableLiveData<AuthStateWithError>()
    val authState: LiveData<AuthStateWithError> = _authState

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    init {
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.addAuthStateListener { auth ->
            val user = auth.currentUser
            _currentUser.postValue(user)

            if (user != null) {
                _authState.postValue(AuthStateWithError(AuthState.AUTHENTICATED))
            } else {
                _authState.postValue(AuthStateWithError(AuthState.UNAUTHENTICATED))
            }
        }
    }

    fun signIn(
        email: String,
        password: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        authManager.signInWithEmailAndPassword(email, password) { success, errorMessage ->

            viewModelScope.launch(Dispatchers.Main) {
                if (success) {
                    _authState.value = AuthStateWithError(AuthState.AUTHENTICATED)
                } else {
                    _authState.value = AuthStateWithError(
                        AuthState.UNAUTHENTICATED,
                        errorMessage
                    )
                }

                onComplete(success, errorMessage)
            }
        }
    }

    fun signUp(
        email: String,
        password: String,
        onComplete: (Boolean, String?, String?) -> Unit
    ) {
        viewModelScope.launch {
            authManager.createUserWithEmailAndPassword(email, password) { success, errorMessage, userId ->

                viewModelScope.launch(Dispatchers.Main) {
                    if (success) {
                        _authState.value = AuthStateWithError(AuthState.AUTHENTICATED)
                    } else {
                        _authState.value = AuthStateWithError(
                            AuthState.UNAUTHENTICATED,
                            errorMessage
                        )
                    }

                    onComplete(success, errorMessage, userId)
                }
            }
        }
    }

    fun signOut() {
        authManager.signOut()
        _authState.value = AuthStateWithError(AuthState.UNAUTHENTICATED)
        _currentUser.value = null
    }
}