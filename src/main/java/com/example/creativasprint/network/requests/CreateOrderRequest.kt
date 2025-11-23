package com.example.creativasprint.network.requests

data class CreateOrderRequest(
    val user_id: String,
    val total: Double,
    val status: String = "pending",
    val customer_name: String,
    val customer_email: String,
    val customer_phone: String,
    val shipping_address: String,
    val shipping_notes: String = "",
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val product_id: String,
    val product_name: String,
    val quantity: Int,
    val price: Double
)