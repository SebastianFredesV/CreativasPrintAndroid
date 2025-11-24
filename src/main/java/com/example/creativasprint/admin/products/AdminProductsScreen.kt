package com.example.creativasprint.admin.products

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
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
import com.example.creativasprint.model.Product

@Composable
fun AdminProductsScreen(navController: NavController) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    // Datos de ejemplo para desarrollo
    val sampleProducts = listOf(
        Product(
            id = 1,
            nombre = "Agenda Alicia en el País de las Maravillas",
            precio = 15000.0,
            color = "Azul",
            descripcion = "Sumérgete en el mágico y oscuro mundo de Tim Burton...",
            imagen = "img/AgendaAliciaimg1.jpg",
            categoria = "Agendas",
            stock = 10,
            isActive = true
        ),
        Product(
            id = 2,
            nombre = "Agenda Beetle Juice",
            precio = 15000.0,
            color = "Blanco y negro",
            descripcion = "Sumérgete en el mágico y oscuro mundo de Tim Burton...",
            imagen = "img/AgendaBeetleJuice.jpg",
            categoria = "Agendas",
            stock = 5,
            isActive = true
        ),
        Product(
            id = 3,
            nombre = "Agenda El cadáver de la novia",
            precio = 18000.0,
            color = "Morado",
            descripcion = "Sumérgete en el mágico y oscuro mundo de Tim Burton...",
            imagen = "img/AgendaCadaverNovia.jpg",
            categoria = "Agendas",
            stock = 8,
            isActive = true
        )
    )

    LaunchedEffect(Unit) {
        products = sampleProducts
    }

    val filteredProducts = if (searchQuery.isEmpty()) {
        products
    } else {
        products.filter { product ->
            product.nombre.contains(searchQuery, ignoreCase = true) ||
                    product.descripcion.contains(searchQuery, ignoreCase = true) ||
                    product.categoria.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("admin_product_form")
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar Producto")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                "Gestión de Productos",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar productos...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredProducts.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No se encontraron productos")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredProducts) { product ->
                        AdminProductCard(
                            product = product,
                            onEdit = {
                                navController.navigate("admin_product_form/${product.id}")
                            },
                            onDelete = {
                                // Lógica para eliminar producto
                                products = products.filter { it.id != product.id }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        product.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("$${String.format("%.0f", product.precio)}")
                    Text("Categoría: ${product.categoria}")
                    Text("Stock: ${product.stock}")
                    Text(
                        "Estado: ${if (product.isActive) "Activo" else "Inactivo"}",
                        color = if (product.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }

                // Botones de acción
                Column {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }

                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                product.descripcion,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}