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
import com.example.creativasprint.auth.SplashScreen  // ✅ AGREGAR ESTA IMPORTACIÓN
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
        startDestination = Destinations.Splash.route
    ) {
        // ✅ SPLASH SCREEN (ahora con importación correcta)
        composable(Destinations.Splash.route) {
            SplashScreen(navController = navController)
        }

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
        composable(Destinations.ProductList.route) {  // ✅ Usar Destinations en lugar de string
            val cartManager = remember { CartManager(context) }
            ProductListScreen(
                navController = navController,
                cartManager = cartManager
            )
        }

        // Client Cart & Checkout Screens
        composable(Destinations.Cart.route) {  // ✅ Usar Destinations
            val cartManager = remember { CartManager(context) }
            val orderManager = remember { OrderManager(context) }
            CartScreen(
                navController = navController,
                cartManager = cartManager,
                orderManager = orderManager
            )
        }

        composable(Destinations.Checkout.route) {  // ✅ Usar Destinations
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
            route = Destinations.OrderConfirmation.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            OrderConfirmationScreen(
                navController = navController,
                orderId = orderId
            )
        }

        // Client Order History
        composable(Destinations.OrderHistory.route) {  // ✅ Usar Destinations
            val orderManager = remember { OrderManager(context) }
            OrderHistoryScreen(
                navController = navController,
                orderManager = orderManager
            )
        }

        // Client Profile
        composable(Destinations.Profile.route) {  // ✅ Usar Destinations
            ProfileScreen(navController = navController)
        }

        // Admin Screens (si los tienes implementados)
        composable(Destinations.AdminMain.route) {
            // Aquí va tu AdminMainScreen cuando lo implementes
            // Por ahora podemos mostrar un placeholder
            ClientMainScreen(navController = navController) // Temporal
        }

        composable(Destinations.AdminProducts.route) {
            // Aquí va tu AdminProductsScreen cuando lo implementes
            ClientMainScreen(navController = navController) // Temporal
        }
    }
}