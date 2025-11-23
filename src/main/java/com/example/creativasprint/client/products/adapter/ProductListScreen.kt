package com.example.creativasprint.client.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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
import com.example.creativasprint.model.Product

@Composable
fun ProductListScreen(navController: NavController) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

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
        )
        // Agrega más productos según tu JSON!!!!!!!!!!!!!!!!!!!!
    )

    LaunchedEffect(Unit) {
        // Simular carga de productos
        kotlinx.coroutines.delay(1000)
        products = sampleProducts
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Nuestros Productos")

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            ProductList(products = products, navController = navController)
        }
    }
}

@Composable
fun ProductList(products: List<Product>, navController: NavController) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { product ->
            ProductCard(product = product, navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(product: Product, navController: NavController) {
    Card(
        onClick = {
            // Navegar al detalle del producto
            // navController.navigate("product_detail/${product.id}")
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = product.nombre, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text(text = "$${product.precio}", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
            Text(text = product.descripcion.take(100) + "...", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
            Text(text = "Color: ${product.color}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
        }
    }
}