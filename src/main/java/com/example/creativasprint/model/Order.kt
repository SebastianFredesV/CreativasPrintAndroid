package com.example.creativasprint.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("items") val items: List<OrderItem>,
    @SerializedName("total") val total: Double,
    @SerializedName("status") val status: String,
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("customer_email") val customerEmail: String,
    @SerializedName("customer_phone") val customerPhone: String,
    @SerializedName("shipping_address") val shippingAddress: String,
    @SerializedName("shipping_notes") val shippingNotes: String = "",
    @SerializedName("created_at") val createdAt: String
) {
    fun getStatusText(): String {
        return when (status) {
            "pending" -> "Pendiente"
            "accepted" -> "Aceptado"
            "rejected" -> "Rechazado"
            "shipped" -> "Enviado"
            else -> "Desconocido"
        }
    }
}