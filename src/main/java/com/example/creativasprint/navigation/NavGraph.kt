package com.example.creativasprint.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.creativasprint.auth.LoginScreen
import com.example.creativasprint.auth.RegisterScreen
import com.example.creativasprint.client.ClientMainScreen
import com.example.creativasprint.client.cart.CartScreen
import com.example.creativasprint.client.checkout.CheckoutScreen
import com.example.creativasprint.client.checkout.OrderConfirmationScreen
import com.example.creativasprint.client.orders.OrderHistoryScreen
import com.example.creativasprint.client.products.ProductListScreen
import com.example.creativasprint.client.profile.ProfileScreen
import com.example.creativasprint.data.CartManager
import com.example.creativasprint.data.OrderManager
import com.example.creativasprint.destinations.Destinations

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Destinations.Login.route
    ) {
        // Auth Screens
        composable(Destinations.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Destinations.Register.route) {
            RegisterScreen(navController = navController)
        }

        // Client Main Screen
        composable(Destinations.ClientMain.route) {
            ClientMainScreen(navController = navController)
        }

        // Client Product Screens
        composable("product_list") {
            val cartManager = remember { CartManager(context) }
            ProductListScreen(
                navController = navController,
                cartManager = cartManager
            )
        }

        // Client Cart & Checkout Screens
        composable("cart") {
            val cartManager = remember { CartManager(context) }
            val orderManager = remember { OrderManager(context) }
            CartScreen(
                navController = navController,
                cartManager = cartManager,
                orderManager = orderManager
            )
        }

        composable("checkout") {
            val cartManager = remember { CartManager(context) }
            val orderManager = remember { OrderManager(context) }
            CheckoutScreen(
                navController = navController,
                cartManager = cartManager,
                orderManager = orderManager
            )
        }

        // Order Confirmation con parámetro
        composable(
            route = "order_confirmation/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            OrderConfirmationScreen(
                navController = navController,
                orderId = orderId
            )
        }

        // Client Order History
        composable("order_history") {
            val orderManager = remember { OrderManager(context) }
            OrderHistoryScreen(
                navController = navController,
                orderManager = orderManager
            )
        }

        // Client Profile
        composable("profile") {
            ProfileScreen(navController = navController)
        }

        // Puedes agregar aquí las rutas del admin si es necesario
    }
}