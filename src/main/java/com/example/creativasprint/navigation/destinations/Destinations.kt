package com.example.creativasprint.destinations

sealed class Destinations(val route: String) {
    object Splash : Destinations("splash")
    object Login : Destinations("login")
    object Register : Destinations("register")
    object AdminMain : Destinations("admin_main")
    object ClientMain : Destinations("client_main")

    // Cliente
    object ProductList : Destinations("product_list")
    object ProductDetail : Destinations("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Cart : Destinations("cart")
    object Checkout : Destinations("checkout")
    object OrderHistory : Destinations("order_history")
    object Profile : Destinations("profile")

    // Admin
    object AdminProducts : Destinations("admin_products")
    object AdminProductForm : Destinations("admin_product_form")
    object AdminUsers : Destinations("admin_users")
    object AdminOrders : Destinations("admin_orders")
}