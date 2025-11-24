package com.example.creativasprint.client.checkout

import android.util.Log
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.creativasprint.data.CartManager
import com.example.creativasprint.data.OrderManager
import com.example.creativasprint.destinations.Destinations
import com.example.creativasprint.model.CartItem
import kotlinx.coroutines.delay

@Composable
fun CheckoutScreen(navController: NavController, cartManager: CartManager, orderManager: OrderManager) {
    val context = LocalContext.current
    val cartManager = remember { CartManager(context) }
    val orderManager = remember { OrderManager(context) }

    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var total by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(false) }
    var shouldProcessOrder by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Datos del formulario
    var customerName by remember { mutableStateOf("") }
    var customerEmail by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var shippingAddress by remember { mutableStateOf("") }
    var shippingNotes by remember { mutableStateOf("") }

    // Datos de prueba para desarrollo (quitar en producción)
    LaunchedEffect(Unit) {
        customerName = "Cliente Ejemplo"
        customerEmail = "cliente@ejemplo.com"
        customerPhone = "123456789"
        shippingAddress = "Calle Falsa 123"

        cartItems = cartManager.getCartItems()
        total = cartManager.getCartTotal()
        Log.d("CheckoutDebug", "Cart items loaded: ${cartItems.size}, Total: $total")
    }

    // LaunchedEffect para procesar el pedido
    if (shouldProcessOrder) {
        LaunchedEffect(shouldProcessOrder) {
            if (shouldProcessOrder) {
                try {
                    Log.d("CheckoutDebug", "Starting order processing...")
                    isLoading = true
                    errorMessage = null

                    // Validar datos antes de procesar
                    if (!validateForm(customerName, customerEmail, customerPhone, shippingAddress)) {
                        errorMessage = "Por favor completa todos los campos correctamente"
                        isLoading = false
                        shouldProcessOrder = false
                        return@LaunchedEffect
                    }

                    if (cartItems.isEmpty()) {
                        errorMessage = "El carrito está vacío"
                        isLoading = false
                        shouldProcessOrder = false
                        return@LaunchedEffect
                    }

                    Log.d("CheckoutDebug", "Creating order...")
                    delay(2000) // Simular proceso de pago

                    val order = orderManager.createOrderFromCart(
                        cartItems = cartItems,
                        total = total,
                        customerName = customerName,
                        customerEmail = customerEmail,
                        customerPhone = customerPhone,
                        shippingAddress = shippingAddress,
                        shippingNotes = shippingNotes
                    )

                    Log.d("CheckoutDebug", "Order created: ${order.id}")

                    orderManager.saveOrder(order)
                    Log.d("CheckoutDebug", "Order saved successfully")

                    cartManager.clearCart()
                    Log.d("CheckoutDebug", "Cart cleared")

                    isLoading = false
                    shouldProcessOrder = false

                    // Navegar a confirmación
                    Log.d("CheckoutDebug", "Navigating to confirmation...")
                    navController.navigate(Destinations.OrderConfirmation.createRoute(order.id)) {
                        popUpTo(Destinations.Checkout.route) { inclusive = true }
                    }

                } catch (e: Exception) {
                    Log.e("CheckoutDebug", "Error processing order: ${e.message}", e)
                    errorMessage = "Error al procesar el pedido: ${e.message}"
                    isLoading = false
                    shouldProcessOrder = false
                }
            }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                "Finalizar Compra",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar mensaje de error
            errorMessage?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (cartItems.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Tu carrito está vacío")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Resumen del pedido
                    item {
                        OrderSummary(cartItems = cartItems, total = total)
                    }

                    // Formulario de datos
                    item {
                        CustomerInfoForm(
                            customerName = customerName,
                            onCustomerNameChange = { customerName = it },
                            customerEmail = customerEmail,
                            onCustomerEmailChange = { customerEmail = it },
                            customerPhone = customerPhone,
                            onCustomerPhoneChange = { customerPhone = it },
                            shippingAddress = shippingAddress,
                            onShippingAddressChange = { shippingAddress = it },
                            shippingNotes = shippingNotes,
                            onShippingNotesChange = { shippingNotes = it }
                        )
                    }

                    // Botón de confirmación
                    item {
                        Button(
                            onClick = {
                                Log.d("CheckoutDebug", "Confirm button clicked")
                                if (validateForm(customerName, customerEmail, customerPhone, shippingAddress)) {
                                    shouldProcessOrder = true
                                } else {
                                    errorMessage = "Por favor completa todos los campos correctamente"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading && validateForm(customerName, customerEmail, customerPhone, shippingAddress)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator()
                            } else {
                                Text("Confirmar Pedido - $${String.format("%.0f", total)}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderSummary(cartItems: List<CartItem>, total: Double) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Resumen del Pedido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // REEMPLAZAMOS EL LAZYCOLUMN INTERNO POR UN COLUMN NORMAL
            Column {
                cartItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.productName} x${item.quantity}")
                        Text("$${String.format("%.0f", item.productPrice * item.quantity)}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:", fontWeight = FontWeight.Bold)
                Text(
                    "$${String.format("%.0f", total)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerInfoForm(
    customerName: String,
    onCustomerNameChange: (String) -> Unit,
    customerEmail: String,
    onCustomerEmailChange: (String) -> Unit,
    customerPhone: String,
    onCustomerPhoneChange: (String) -> Unit,
    shippingAddress: String,
    onShippingAddressChange: (String) -> Unit,
    shippingNotes: String,
    onShippingNotesChange: (String) -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Datos de Envío",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = customerName,
                onValueChange = onCustomerNameChange,
                label = { Text("Nombre completo *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = customerName.isEmpty()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customerEmail,
                onValueChange = onCustomerEmailChange,
                label = { Text("Email *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = customerEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(customerEmail).matches()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customerPhone,
                onValueChange = onCustomerPhoneChange,
                label = { Text("Teléfono *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                isError = customerPhone.isEmpty()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = shippingAddress,
                onValueChange = onShippingAddressChange,
                label = { Text("Dirección de envío *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3,
                isError = shippingAddress.isEmpty()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = shippingNotes,
                onValueChange = onShippingNotesChange,
                label = { Text("Instrucciones de entrega (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3
            )
        }
    }
}

private fun validateForm(
    name: String,
    email: String,
    phone: String,
    address: String
): Boolean {
    return name.isNotEmpty() &&
            email.isNotEmpty() &&
            phone.isNotEmpty() &&
            address.isNotEmpty() &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}