package com.example.creativasprint.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("items") val items: List<OrderItem>? = emptyList(), // ✅ Permitir null
    @SerializedName("total") val total: Double,
    @SerializedName("status") val status: String,
    @SerializedName("customer_name") val customerName: String? = null, // ✅ Permitir null
    @SerializedName("customer_email") val customerEmail: String? = null, // ✅ Permitir null
    @SerializedName("customer_phone") val customerPhone: String? = null, // ✅ Permitir null
    @SerializedName("shipping_address") val shippingAddress: String? = null, // ✅ Permitir null
    @SerializedName("shipping_notes") val shippingNotes: String? = null, // ✅ Permitir null
    @SerializedName("created_at") val createdAt: String? = null // ✅ Permitir null
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