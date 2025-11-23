package com.example.creativasprint.client.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.creativasprint.data.OrderManager

@Composable
fun OrderConfirmationScreen(navController: NavController, orderId: String?) {
    val context = LocalContext.current
    val orderManager = remember { OrderManager(context) }

    val order = orderId?.let { orderManager.getOrderById(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Pedido confirmado",
            modifier = Modifier.height(80.dp).padding(8.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            "¡Pedido Confirmado!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Tu pedido ha sido procesado exitosamente",
            style = MaterialTheme.typography.bodyLarge
        )

        order?.let {
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Resumen del Pedido", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Número: #${it.id.take(8)}")
                    Text("Total: $${String.format("%.0f", it.total)}")
                    Text("Estado: ${it.getStatusText()}")
                    Text("Fecha: ${it.createdAt}")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.navigate("client_main") {
                    popUpTo("client_main") { inclusive = true }
                }
            }
        ) {
            Text("Volver al Inicio")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate("order_history")
            }
        ) {
            Text("Ver Mis Pedidos")
        }
    }
}