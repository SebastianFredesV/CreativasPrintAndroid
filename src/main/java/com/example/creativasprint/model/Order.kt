package com.example.creativasprint.model

data class Order(
    val id: String = System.currentTimeMillis().toString(),
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val total: Double = 0.0,
    val status: String = "pending", // "pending", "accepted", "rejected", "shipped"
    val createdAt: String = "",
    val shippingAddress: String = "",
    val customerName: String = "",
    val customerEmail: String = "",
    val customerPhone: String = "",
    val shippingNotes: String = ""
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