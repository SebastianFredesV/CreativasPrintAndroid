package com.example.creativasprint.network.requests

data class UpdateOrderStatusRequest(
    val status: String // "pending", "accepted", "rejected", "shipped"
)