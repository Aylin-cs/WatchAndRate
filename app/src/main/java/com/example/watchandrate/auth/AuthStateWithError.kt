package com.example.watchandrate.auth

data class AuthStateWithError(
    val authState: AuthState,
    val errorMessage: String? = null
)