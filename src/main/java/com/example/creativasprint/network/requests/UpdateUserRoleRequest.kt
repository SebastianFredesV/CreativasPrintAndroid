package com.example.creativasprint.network.requests

import com.google.gson.annotations.SerializedName

data class UpdateUserRoleRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("new_role") val newRole: String
)