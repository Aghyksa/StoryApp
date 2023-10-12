package com.aghyksa.storyapp.model


data class LoginResponse(
    val loginResult: LoginResult,
    val error: Boolean,
    val message: String
)
