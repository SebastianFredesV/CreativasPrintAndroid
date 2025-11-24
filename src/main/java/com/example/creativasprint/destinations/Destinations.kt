package com.example.creativasprint.destinations

object Destinations {
    const val SPLASH = "splashFragment"
    const val LOGIN = "loginFragment"
    const val REGISTER = "registerFragment"
    const val CLIENT_MAIN = "clientMainFragment"
    const val PRODUCT_LIST = "productListFragment"
    const val CART = "cartFragment"
    const val CHECKOUT = "checkoutFragment"
    const val ORDER_CONFIRMATION = "orderConfirmationFragment"
    const val ORDER_HISTORY = "orderHistoryFragment"
    const val PROFILE = "profileFragment"
    const val ADMIN_MAIN = "adminMainFragment"
    const val ADMIN_PRODUCTS = "adminProductsFragment"
    const val ADMIN_ORDERS = "adminOrdersFragment"
    const val ADMIN_USERS = "adminUsersFragment"

    // Método para crear ruta con argumento
    fun createOrderConfirmationRoute(orderId: String): String {
        return "orderConfirmationFragment?orderId=$orderId"
    }
}