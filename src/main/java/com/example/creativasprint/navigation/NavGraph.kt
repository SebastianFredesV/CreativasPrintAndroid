package com.example.creativasprint.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.creativasprint.admin.AdminMainScreen
import com.example.creativasprint.admin.products.AdminProductsScreen
import com.example.creativasprint.auth.LoginScreen
import com.example.creativasprint.auth.RegisterScreen
import com.example.creativasprint.auth.SplashScreen
import com.example.creativasprint.client.ClientMainScreen
import com.example.creativasprint.client.cart.CartScreen
import com.example.creativasprint.client.checkout.CheckoutScreen
import com.example.creativasprint.client.checkout.OrderConfirmationScreen
import com.example.creativasprint.client.orders.OrderHistoryScreen
import com.example.creativasprint.client.products.ProductListScreen
import com.example.creativasprint.destinations.Destinations
import com.example.creativasprint.admin.products.AdminProductFormScreen
import com.example.creativasprint.admin.users.AdminUsersScreen
import com.example.creativasprint.admin.orders.AdminOrdersScreen
import com.example.creativasprint.client.profile.ProfileScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.Splash.route
    ) {
        composable(Destinations.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Destinations.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Destinations.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Destinations.AdminMain.route) {
            AdminMainScreen(navController = navController)
        }
        composable(Destinations.ClientMain.route) {
            ClientMainScreen(navController = navController)
        }
        composable(Destinations.ProductList.route) {
            ProductListScreen(navController = navController)
        }
        composable(Destinations.Cart.route) {
            CartScreen(navController = navController)
        }
        composable(Destinations.Checkout.route) {
            CheckoutScreen(navController = navController)
        }
        composable("order_confirmation/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            OrderConfirmationScreen(navController = navController, orderId = orderId)
        }
        composable(Destinations.OrderHistory.route) {
            OrderHistoryScreen(navController = navController)
        }

        // Rutas de Administración
        composable(Destinations.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Destinations.AdminProducts.route) {
            AdminProductsScreen(navController = navController)
        }
        composable(Destinations.AdminUsers.route) {
            AdminUsersScreen(navController = navController)
        }
        composable(Destinations.AdminOrders.route) {
            AdminOrdersScreen(navController = navController)
        }
        composable("admin_product_form/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            AdminProductFormScreen(navController = navController, productId = productId)
        }

        composable("admin_product_form") {
            AdminProductFormScreen(navController = navController, productId = null)
        }
    }
}