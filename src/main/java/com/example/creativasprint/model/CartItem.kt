package com.example.creativasprint.model

data class CartItem(
    val id: Int,
    val productId: Int,
    val productName: String,
    val productPrice: Double,
    val productImage: String, // Mantener como String para simplificar
    val quantity: Int
)