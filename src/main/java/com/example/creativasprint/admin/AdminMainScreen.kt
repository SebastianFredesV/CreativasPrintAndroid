package com.example.creativasprint.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
        Text(
            "Panel de Administración",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        currentUser?.let { user ->
            Text("Bienvenido, ${user.name}")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjetas de funcionalidades
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gestión de Productos
            AdminFeatureCard(
                title = "Gestión de Productos",
                description = "Crear, editar y eliminar productos del catálogo",
                icon = Icons.Filled.Inventory,
                onClick = { navController.navigate(Destinations.AdminProducts.route) }
            )

            // Gestión de Usuarios
            AdminFeatureCard(
                title = "Gestión de Usuarios",
                description = "Administrar usuarios y permisos",
                icon = Icons.Filled.People,
                onClick = { navController.navigate(Destinations.AdminUsers.route) }
            )

            // Gestión de Pedidos
            AdminFeatureCard(
                title = "Gestión de Pedidos",
                description = "Revisar y gestionar pedidos de clientes",
                icon = Icons.Filled.ShoppingCart,
                onClick = { navController.navigate(Destinations.AdminOrders.route) }
            )
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

@Composable
fun AdminFeatureCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}