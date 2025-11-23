package com.example.creativasprint.model

data class CartItem(
    val id: String,
    val productId: String,
    val productName: String,
    val productPrice: Double,
    val productImage: String,
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
)