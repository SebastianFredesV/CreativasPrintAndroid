package com.example.creativasprint.admin

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
fun AdminMainScreen(navController: NavController) {
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
        Text("Panel de Administración")

        Spacer(modifier = Modifier.height(8.dp))

        currentUser?.let { user ->
            Text("Bienvenido, ${user.name}")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Funciones disponibles:")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: Gestión de productos */ }
        ) {
            Text("Gestionar Productos")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* TODO: Gestión de usuarios */ }
        ) {
            Text("Gestionar Usuarios")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* TODO: Gestión de pedidos */ }
        ) {
            Text("Gestionar Pedidos")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                sessionManager.logout()
                navController.navigate(Destinations.Login.route) {
                    popUpTo(Destinations.AdminMain.route) { inclusive = true }
                }
            }
        ) {
            Text("Cerrar Sesión")
        }
    }
}