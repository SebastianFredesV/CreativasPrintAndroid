package com.example.creativasprint.network.responses

import com.google.gson.annotations.SerializedName

data class XanoAuthResponse(
    @SerializedName("authToken") val authToken: String,
    @SerializedName("user_id") val userId: Int
)