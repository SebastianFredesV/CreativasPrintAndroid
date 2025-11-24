package com.example.creativasprint.data

import android.content.Context
import android.content.SharedPreferences
import com.example.creativasprint.model.CartItem
import com.example.creativasprint.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_CART_ITEMS = "cart_items"
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        val currentItems = getCartItems().toMutableList()
        val existingItem = currentItems.find { it.productId == product.id }

        if (existingItem != null) {
            updateQuantity(product.id, existingItem.quantity + quantity)
        } else {
            val newItem = CartItem(
                id = System.currentTimeMillis().toInt(),
                productId = product.id,
                productName = product.nombre,
                productPrice = product.precio,
                productImage = product.getImageUrl(), // Usar la URL de la imagen
                quantity = quantity
            )
            currentItems.add(newItem)
            saveCartItems(currentItems)
        }
    }

    fun getCartItems(): List<CartItem> {
        val json = sharedPreferences.getString(KEY_CART_ITEMS, null)
        return if (json != null) {
            val type = object : TypeToken<List<CartItem>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun updateQuantity(productId: Int, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(productId)
            return
        }

        val currentItems = getCartItems().toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.productId == productId }

        if (itemIndex != -1) {
            currentItems[itemIndex] = currentItems[itemIndex].copy(quantity = newQuantity)
            saveCartItems(currentItems)
        }
    }

    fun removeFromCart(productId: Int) {
        val currentItems = getCartItems().toMutableList()
        currentItems.removeAll { it.productId == productId }
        saveCartItems(currentItems)
    }

    fun clearCart() {
        sharedPreferences.edit().remove(KEY_CART_ITEMS).apply()
    }

    fun getCartTotal(): Double {
        return getCartItems().sumOf { it.productPrice * it.quantity }
    }

    fun getCartItemsCount(): Int {
        return getCartItems().sumOf { it.quantity }
    }

    private fun saveCartItems(items: List<CartItem>) {
        val json = gson.toJson(items)
        sharedPreferences.edit().putString(KEY_CART_ITEMS, json).apply()
    }
}