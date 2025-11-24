package com.example.creativasprint.admin.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.creativasprint.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AdminProductsScreen(navController: NavController) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar productos reales desde Xano
    LaunchedEffect(Unit) {
        loadProductsFromApi(
            onLoading = { isLoading = it },
            onSuccess = {
                products = it
                isLoading = false
            },
            onError = {
                errorMessage = it
                isLoading = false
            }
        )
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

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error al cargar productos")
                            Text(errorMessage ?: "Error desconocido")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    isLoading = true
                                    errorMessage = null
                                    loadProductsFromApi(
                                        onLoading = { isLoading = it },
                                        onSuccess = {
                                            products = it
                                            isLoading = false
                                        },
                                        onError = {
                                            errorMessage = it
                                            isLoading = false
                                        }
                                    )
                                }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                filteredProducts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No se encontraron productos")
                            if (products.isEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Agrega tu primer producto usando el botón +")
                            }
                        }
                    }
                }

                else -> {
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
                                    // Lógica para eliminar producto de la API
                                    deleteProductFromApi(product.id) { success ->
                                        if (success) {
                                            // Actualizar lista local
                                            products = products.filter { it.id != product.id }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Función para cargar productos desde la API
private fun loadProductsFromApi(
    onLoading: (Boolean) -> Unit,
    onSuccess: (List<Product>) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = ApiClient.apiService.getProducts()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val productsList = response.body() ?: emptyList()
                    // Filtrar productos válidos
                    val validProducts = productsList.filter {
                        it.isValid() && it.nombre.isNotEmpty() && it.precio > 0
                    }
                    onSuccess(validProducts)
                } else {
                    onError("Error ${response.code()} al cargar productos: ${response.message()}")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Error de conexión: ${e.message}")
            }
        }
    }
}

// Función para eliminar producto desde la API
private fun deleteProductFromApi(productId: Int, onResult: (Boolean) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = ApiClient.apiService.deleteProduct(productId)

            withContext(Dispatchers.Main) {
                onResult(response.isSuccessful)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult(false)
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

            // Mostrar imagen (opcional - para debug)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Imagen: ${product.getImageUrl().take(50)}...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}