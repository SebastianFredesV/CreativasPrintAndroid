package com.example.creativasprint.network.responses

import com.example.creativasprint.model.User

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: User?,
    val token: String? = null
)