package com.example.creativasprint.network.requests

import com.google.gson.annotations.SerializedName

data class UpdateUserProfileRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("is_active") val isActive: Boolean? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("address") val address: String? = null
)