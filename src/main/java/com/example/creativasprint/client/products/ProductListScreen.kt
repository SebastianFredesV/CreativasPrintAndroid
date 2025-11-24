package com.example.creativasprint.client.products

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Box
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
                            // Podrías agregar un botón de reintento aquí
                        }
                    }
                }
                filteredProducts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No se encontraron productos")
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
                    // Filtrar solo productos activos
                    val activeProducts = productsList.filter { it.isActive }
                    onSuccess(activeProducts)
                } else {
                    onError("Error al cargar productos: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Error de conexión: ${e.message}")
            }
        }
    }
}

// Componente ProductCard (si no lo tienes, aquí está una versión básica)
@Composable
fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit,
    onProductClick: () -> Unit
) {
    // Aquí va tu implementación de ProductCard
    // Debe mostrar: imagen, nombre, precio, botón "Agregar al carrito"
    // Usa Coil para cargar imágenes: rememberImagePainter(product.imagen)
}