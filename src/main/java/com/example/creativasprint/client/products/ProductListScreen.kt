package com.example.creativasprint.client.products

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.creativasprint.data.CartManager
import com.example.creativasprint.model.Product
import com.example.creativasprint.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(navController: NavController, cartManager: CartManager) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var filteredProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar productos al iniciar
    LaunchedEffect(Unit) {
        loadProducts(
            onLoading = { isLoading = it },
            onSuccess = {
                products = it
                filteredProducts = it
                isLoading = false
            },
            onError = {
                errorMessage = it
                isLoading = false
            }
        )
    }

    // Filtrar productos cuando cambia la búsqueda
    LaunchedEffect(searchQuery, products) {
        filteredProducts = if (searchQuery.isEmpty()) {
            products
        } else {
            products.filter { product ->
                product.nombre.contains(searchQuery, ignoreCase = true) ||
                        product.descripcion.contains(searchQuery, ignoreCase = true) ||
                        product.categoria.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de búsqueda
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar productos...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

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
                                    loadProducts(
                                        onLoading = { isLoading = it },
                                        onSuccess = {
                                            products = it
                                            filteredProducts = it
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
                                Text("Verifica que hay productos en la base de datos")
                            }
                        }
                    }
                }

                else -> {
                    // Grid de productos
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        items(filteredProducts) { product ->
                            ProductCard(
                                product = product,
                                onAddToCart = {
                                    cartManager.addToCart(product)
                                    // Podrías mostrar un snackbar de confirmación
                                },
                                onProductClick = {
                                    // Navegar a detalle del producto si lo implementas
                                    // navController.navigate("product_detail/${product.id}")
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
private fun loadProducts(
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
                    println("DEBUG: Productos recibidos: ${productsList.size}")

                    // Filtrar productos válidos (elimina productos vacíos o inválidos)
                    val validProducts = productsList.filter {
                        it.isValid() && it.nombre.isNotEmpty() && it.precio > 0
                    }

                    println("DEBUG: Productos válidos: ${validProducts.size}")
                    validProducts.forEach { product ->
                        println("DEBUG: Producto: ${product.nombre}, Imagen: ${product.getImageUrl()}")
                    }

                    onSuccess(validProducts)
                } else {
                    onError("Error ${response.code()} al cargar productos: ${response.message()}")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                println("DEBUG: Excepción: ${e.message}")
                onError("Error de conexión: ${e.message}")
            }
        }
    }
}

// Componente ProductCard
@Composable
fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit,
    onProductClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = onProductClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Imagen del producto
            AsyncImage(
                model = product.getImageUrl(),
                contentDescription = product.nombre,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre del producto
            Text(
                text = product.nombre,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Precio
            Text(
                text = "$${String.format("%.0f", product.precio)}",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Categoría
            Text(
                text = product.categoria,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón agregar al carrito
            Button(
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar al Carrito")
            }
        }
    }
}