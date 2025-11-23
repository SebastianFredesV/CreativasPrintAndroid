package com.example.creativasprint.admin.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.creativasprint.model.Product
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductFormScreen(navController: NavController, productId: String?) {
    var isLoading by remember { mutableStateOf(false) }
    val isEditing by remember { mutableStateOf(productId != null) }

    // Estado del formulario
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    // Get a coroutine scope
    val scope = rememberCoroutineScope()

    // Cargar datos del producto si estamos editando
    LaunchedEffect(productId) {
        if (productId != null) {
            // Simular carga de producto (en una app real, esto vendría de una API)
            isLoading = true
            // Datos de ejemplo para edición
            nombre = "Agenda Alicia en el País de las Maravillas"
            descripcion = "Sumérgete en el mágico y oscuro mundo de Tim Burton con esta hermosa agenda..."
            precio = "15000"
            color = "Azul"
            categoria = "Agendas"
            stock = "10"
            imagen = "img/AgendaAliciaimg1.jpg"
            isActive = true
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Producto" else "Nuevo Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            if (isLoading && productId != null) { // Show indicator only on initial load
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                ProductForm(
                    nombre = nombre,
                    onNombreChange = { nombre = it },
                    descripcion = descripcion,
                    onDescripcionChange = { descripcion = it },
                    precio = precio,
                    onPrecioChange = { precio = it },
                    color = color,
                    onColorChange = { color = it },
                    categoria = categoria,
                    onCategoriaChange = { categoria = it },
                    stock = stock,
                    onStockChange = { stock = it },
                    imagen = imagen,
                    onImagenChange = { imagen = it },
                    isActive = isActive,
                    onIsActiveChange = { isActive = it },
                    onSave = {
                        // Lógica para guardar producto
                        scope.launch {
                            isLoading = true
                            // Simular guardado
                            kotlinx.coroutines.delay(1500)
                            isLoading = false
                            navController.popBackStack()
                        }
                    },
                    isEditing = isEditing,
                    isLoading = isLoading
                )
            }
        }
    }
}

@Composable
fun ProductForm(
    nombre: String,
    onNombreChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    precio: String,
    onPrecioChange: (String) -> Unit,
    color: String,
    onColorChange: (String) -> Unit,
    categoria: String,
    onCategoriaChange: (String) -> Unit,
    stock: String,
    onStockChange: (String) -> Unit,
    imagen: String,
    onImagenChange: (String) -> Unit,
    isActive: Boolean,
    onIsActiveChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    isEditing: Boolean,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Información básica
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Información Básica",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = onNombreChange,
                    label = { Text("Nombre del producto *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = onDescripcionChange,
                    label = { Text("Descripción *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 4
                )
            }
        }

        // Precio y stock
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Precio y Stock",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = precio,
                        onValueChange = onPrecioChange,
                        label = { Text("Precio *") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = stock,
                        onValueChange = onStockChange,
                        label = { Text("Stock *") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
        }

        // Categoría y color
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Categoría y Color",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = categoria,
                    onValueChange = onCategoriaChange,
                    label = { Text("Categoría *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = color,
                    onValueChange = onColorChange,
                    label = { Text("Color *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        // Imagen y estado
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Imagen y Estado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = imagen,
                    onValueChange = onImagenChange,
                    label = { Text("URL de imagen") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Producto activo")
                    Switch(
                        checked = isActive,
                        onCheckedChange = onIsActiveChange
                    )
                }
            }
        }

        // Botón guardar
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && validateProductForm(
                nombre, descripcion, precio, color, categoria, stock
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(if (isEditing) "Actualizar Producto" else "Crear Producto")
            }
        }
    }
}

private fun validateProductForm(
    nombre: String,
    descripcion: String,
    precio: String,
    color: String,
    categoria: String,
    stock: String
): Boolean {
    return nombre.isNotEmpty() &&
            descripcion.isNotEmpty() &&
            precio.isNotEmpty() &&
            color.isNotEmpty() &&
            categoria.isNotEmpty() &&
            stock.isNotEmpty() &&
            precio.toDoubleOrNull() != null &&
            stock.toIntOrNull() != null
}