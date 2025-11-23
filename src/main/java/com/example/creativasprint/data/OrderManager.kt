package com.example.creativasprint.data

import android.content.Context
import android.content.SharedPreferences
import com.example.creativasprint.model.Order
import com.example.creativasprint.model.CartItem
import com.example.creativasprint.model.OrderItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class OrderManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("OrderPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    companion object {
        private const val KEY_ORDERS = "user_orders"
    }

    fun createOrderFromCart(
        cartItems: List<CartItem>,
        total: Double,
        customerName: String,
        customerEmail: String,
        customerPhone: String,
        shippingAddress: String,
        shippingNotes: String = ""
    ): Order {
        val orderItems = cartItems.map { cartItem ->
            OrderItem(
                productId = cartItem.productId,
                productName = cartItem.productName,
                quantity = cartItem.quantity,
                price = cartItem.productPrice
            )
        }

        // Generar un ID único temporal para la orden local
        val temporaryOrderId = UUID.randomUUID().toString()

        return Order(
            id = temporaryOrderId,
            userId = "current_user", // Esto se reemplazará con el ID real del usuario cuando se envíe a la API
            items = orderItems,
            total = total,
            status = "pending",
            customerName = customerName,
            customerEmail = customerEmail,
            customerPhone = customerPhone,
            shippingAddress = shippingAddress,
            shippingNotes = shippingNotes,
            createdAt = dateFormat.format(Date())
        )
    }

    fun saveOrder(order: Order) {
        val currentOrders = getOrders().toMutableList()
        currentOrders.add(0, order)
        saveOrders(currentOrders)
    }

    fun getOrders(): List<Order> {
        val json = sharedPreferences.getString(KEY_ORDERS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Order>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun getOrderById(orderId: String): Order? {
        return getOrders().find { it.id == orderId }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        val currentOrders = getOrders().toMutableList()
        val orderIndex = currentOrders.indexOfFirst { it.id == orderId }

        if (orderIndex != -1) {
            val updatedOrder = currentOrders[orderIndex].copy(status = newStatus)
            currentOrders[orderIndex] = updatedOrder
            saveOrders(currentOrders)
        }
    }

    private fun saveOrders(orders: List<Order>) {
        val json = gson.toJson(orders)
        sharedPreferences.edit().putString(KEY_ORDERS, json).apply()
    }
}