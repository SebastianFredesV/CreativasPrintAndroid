package com.example.creativasprint.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.creativasprint.admin.AdminMainScreen
import com.example.creativasprint.auth.LoginScreen
import com.example.creativasprint.auth.RegisterScreen
import com.example.creativasprint.auth.SplashScreen
import com.example.creativasprint.client.ClientMainScreen
import com.example.creativasprint.client.products.ProductListScreen
import com.example.creativasprint.destinations.Destinations
import com.example.creativasprint.client.cart.CartScreen
import com.example.creativasprint.client.checkout.CheckoutScreen
import com.example.creativasprint.client.checkout.OrderConfirmationScreen
import com.example.creativasprint.client.orders.OrderHistoryScreen

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
    }
}