package com.example.creativasprint.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val isActive: Boolean = true,
    val phone: String? = null,
    val address: String? = null
)