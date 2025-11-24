package com.example.creativasprint.model

import com.google.gson.annotations.SerializedName

data class OrderItem(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price") val price: Double
)