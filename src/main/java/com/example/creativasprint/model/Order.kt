package com.example.creativasprint.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val total: Double = 0.0,
    val status: String = "pending", // "pending", "accepted", "rejected", "shipped"
    val createdAt: String = "",
    val shippingAddress: String = "",
    val customerName: String = "",
    val customerEmail: String = ""
)

data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double
)