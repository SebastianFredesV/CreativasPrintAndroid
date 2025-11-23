package com.example.creativasprint.client.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.creativasprint.data.CartManager
import com.example.creativasprint.model.Product
import kotlinx.coroutines.delay

@Composable
fun ProductListScreen(navController: NavController) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val cartManager = remember { CartManager(context) }

    // Datos de ejemplo
    val sampleProducts = listOf(
        Product(
            id = "1",
            nombre = "Agenda Alicia en el País de las Maravillas",
            precio = 15000.0,
            color = "Azul",
            descripcion = "Sumérgete en el mágico y oscuro mundo de Tim Burton con esta hermosa agenda...",
            imagen = "img/AgendaAliciaimg1.jpg",
            categoria = "Agendas"
        ),
        Product(
            id = "2",
            nombre = "Agenda Beetle Juice",
            precio = 15000.0,
            color = "Blanco y negro",
            descripcion = "Sumérgete en el mágico y oscuro mundo de Tim Burton con esta hermosa agenda...",
            imagen = "img/AgendaBeetleJuice.jpg",
            categoria = "Agendas"
        ),
        Product(
            id = "3",
            nombre = "Agenda El cadáver de la novia",
            precio = 18000.0,
            color = "Morado",
            descripcion = "Sumérgete en el mágico y oscuro mundo de Tim Burton con esta hermosa agenda...",
            imagen = "img/AgendaCadaverNovia.jpg",
            categoria = "Agendas"
        ),
        Product(
            id = "4",
            nombre = "Agenda Coraline",
            precio = 18000.0,
            color = "Azul",
            descripcion = "Sumérgete en el mágico y oscuro mundo de Tim Burton con esta hermosa agenda...",
            imagen = "img/AgendaCoraline.jpg",
            categoria = "Agendas"
        )
    )

    LaunchedEffect(Unit) {
        delay(1000)
        products = sampleProducts
        isLoading = false
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("cart") }
            ) {
                Icon(Icons.Filled.AddShoppingCart, contentDescription = "Carrito")
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
                "Nuestros Productos",
                style = MaterialTheme.typography.headlineMedium
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                ProductList(
                    products = products,
                    navController = navController,
                    onAddToCart = { product ->
                        cartManager.addToCart(product)
                    }
                )
            }
        }
    }
}

@Composable
fun ProductList(
    products: List<Product>,
    navController: NavController,
    onAddToCart: (Product) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                navController = navController,
                onAddToCart = onAddToCart
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: Product,
    navController: NavController,
    onAddToCart: (Product) -> Unit
) {
    Card(
        onClick = {
            // Navegar al detalle del producto
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = product.nombre,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$${String.format("%.0f", product.precio)}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = product.descripcion.take(100) + "...",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Color: ${product.color}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onAddToCart(product) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar al Carrito")
            }
        }
    }
}