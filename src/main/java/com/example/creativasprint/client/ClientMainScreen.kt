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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.creativasprint.auth.SessionManager
import com.example.creativasprint.destinations.Destinations

@Composable
fun ClientMainScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
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

        Spacer(modifier = Modifier.height(24.dp))

        Text("Funciones disponibles:")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("product_list")
            }
        ) {
            Text("Ver Catálogo")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate("cart")
            }
        ) {
            Text("Mi Carrito")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate("order_history")
            }
        ) {
            Text("Mis Pedidos")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate("profile")
            }
        ) {
            Text("Mi Perfil")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                sessionManager.logout()
                navController.navigate(Destinations.Login.route) {
                    popUpTo(Destinations.ClientMain.route) { inclusive = true }
                }
            }
        ) {
            Text("Cerrar Sesión")
        }
    }
}