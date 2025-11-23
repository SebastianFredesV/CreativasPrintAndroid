package com.example.creativasprint.network.requests

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val role: String = "client"
)