package com.example.creativasprint.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("address") val address: String? = null
)