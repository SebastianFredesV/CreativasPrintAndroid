package com.example.creativasprint.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.creativasprint.auth.SessionManager
import com.example.creativasprint.data.CartManager
import com.example.creativasprint.destinations.Destinations

@Composable
fun ClientMainScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val cartManager = remember { CartManager(context) }
    val currentUser = sessionManager.getCurrentUser()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido a CreativasPrint")

        Spacer(modifier = Modifier.height(8.dp))

        currentUser?.let { user ->
            Text("Hola, ${user.name}")
        }

        // Mostrar cantidad de items en el carrito
        val cartItemsCount = cartManager.getCartItemsCount()
        if (cartItemsCount > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tienes $cartItemsCount items en tu carrito")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Funciones disponibles:")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Navegar al catálogo de productos pasando el cartManager
                navController.navigate("product_list")
            }
        ) {
            Text("Ver Catálogo")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // Navegar al carrito
                navController.navigate("cart")
            }
        ) {
            Text("Mi Carrito (${cartItemsCount})")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // Navegar al historial de pedidos
                navController.navigate("order_history")
            }
        ) {
            Text("Mis Pedidos")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // Navegar al perfil
                navController.navigate("profile")
            }
        ) {
            Text("Mi Perfil")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                sessionManager.logout()
                cartManager.clearCart() // Limpiar carrito al cerrar sesión
                navController.navigate(Destinations.Login.route) {
                    popUpTo(Destinations.ClientMain.route) { inclusive = true }
                }
            }
        ) {
            Text("Cerrar Sesión")
        }
    }
}