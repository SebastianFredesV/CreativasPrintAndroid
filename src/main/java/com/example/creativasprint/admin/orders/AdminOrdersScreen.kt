package com.example.creativasprint.admin.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.creativasprint.data.OrderManager
import com.example.creativasprint.model.Order
import androidx.compose.ui.platform.LocalContext

@Composable
fun AdminOrdersScreen(navController: NavController) {
    val context = LocalContext.current
    val orderManager = remember { OrderManager(context) }

    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("all") } // "all", "pending", "accepted", "rejected", "shipped"

    LaunchedEffect(Unit) {
        // Cargar pedidos existentes
        orders = orderManager.getOrders()
    }

    val filteredOrders = orders.filter { order ->
        val matchesSearch = searchQuery.isEmpty() ||
                (order.customerName?.contains(searchQuery, ignoreCase = true) == true) || // ✅ Manejar null
                order.id.contains(searchQuery, ignoreCase = true)

        val matchesStatus = when (filterStatus) {
            "all" -> true
            else -> order.status == filterStatus
        }

        matchesSearch && matchesStatus
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                "Gestión de Pedidos",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar pedidos...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filtros por estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusFilterChip(
                    label = "Todos",
                    isSelected = filterStatus == "all",
                    onClick = { filterStatus = "all" }
                )
                StatusFilterChip(
                    label = "Pendientes",
                    isSelected = filterStatus == "pending",
                    onClick = { filterStatus = "pending" }
                )
                StatusFilterChip(
                    label = "Aceptados",
                    isSelected = filterStatus == "accepted",
                    onClick = { filterStatus = "accepted" }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusFilterChip(
                    label = "Rechazados",
                    isSelected = filterStatus == "rejected",
                    onClick = { filterStatus = "rejected" }
                )
                StatusFilterChip(
                    label = "Enviados",
                    isSelected = filterStatus == "shipped",
                    onClick = { filterStatus = "shipped" }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredOrders.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No se encontraron pedidos")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredOrders) { order ->
                        AdminOrderCard(
                            order = order,
                            onUpdateStatus = { newStatus ->
                                orderManager.updateOrderStatus(order.id, newStatus)
                                // Actualizar la lista
                                orders = orderManager.getOrders()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp),
        colors = if (isSelected) {
            androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        } else {
            androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    ) {
        Text(
            label,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AdminOrderCard(
    order: Order,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con información básica
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Pedido #${order.id.take(8)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        order.createdAt ?: "Fecha no disponible", // ✅ Manejar null
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    order.getStatusText(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (order.status) {
                        "accepted" -> MaterialTheme.colorScheme.primary
                        "rejected" -> MaterialTheme.colorScheme.error
                        "shipped" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información del cliente - ✅ Manejar nulls
            Text(
                "Cliente: ${order.customerName ?: "Cliente no disponible"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Email: ${order.customerEmail ?: "Email no disponible"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Teléfono: ${order.customerPhone ?: "Teléfono no disponible"}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dirección de envío - ✅ Manejar null
            Text(
                "Dirección: ${order.shippingAddress ?: "Dirección no disponible"}",
                style = MaterialTheme.typography.bodySmall
            )
            if (!order.shippingNotes.isNullOrEmpty()) { // ✅ Manejar null o vacío
                Text("Notas: ${order.shippingNotes}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Resumen de productos - ✅ Manejar lista nula
            Text("Productos:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)

            val items = order.items ?: emptyList() // ✅ Si items es null, usar lista vacía
            items.take(2).forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${item.productName ?: "Producto"} x${item.quantity}") // ✅ Manejar null
                    Text("$${String.format("%.0f", item.price * item.quantity)}")
                }
            }

            if (items.size > 2) {
                Text("... y ${items.size - 2} productos más", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:", fontWeight = FontWeight.Bold)
                Text(
                    "$${String.format("%.0f", order.total)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botones de acción según el estado
            when (order.status) {
                "pending" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { onUpdateStatus("rejected") },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Rechazar")
                            Text("Rechazar")
                        }

                        Button(
                            onClick = { onUpdateStatus("accepted") }
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Aceptar")
                            Text("Aceptar")
                        }
                    }
                }
                "accepted" -> {
                    Button(
                        onClick = { onUpdateStatus("shipped") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.LocalShipping, contentDescription = "Marcar como enviado")
                        Text("Marcar como Enviado")
                    }
                }
                "rejected", "shipped" -> {
                    Text(
                        "Pedido ${order.getStatusText().lowercase()}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = when (order.status) {
                            "shipped" -> MaterialTheme.colorScheme.secondary
                            "rejected" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
    }
}