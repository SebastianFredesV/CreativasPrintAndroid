package com.example.creativasprint.client.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.creativasprint.data.CartManager
import com.example.creativasprint.data.OrderManager
import com.example.creativasprint.model.CartItem

@Composable
fun CartScreen(
    navController: NavController,
    cartManager: CartManager,
    orderManager: OrderManager // Aunque no se use aquí, se pasa para consistencia
) {
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var total by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        cartItems = cartManager.getCartItems()
        total = cartManager.getCartTotal()
    }

    // Actualizar cuando cambien los datos
    LaunchedEffect(cartManager) {
        cartItems = cartManager.getCartItems()
        total = cartManager.getCartTotal()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mi Carrito",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (cartItems.isEmpty()) {
            EmptyCartState()
        } else {
            CartItemsList(
                cartItems = cartItems,
                onQuantityChange = { productId, newQuantity ->
                    cartManager.updateQuantity(productId, newQuantity)
                    cartItems = cartManager.getCartItems()
                    total = cartManager.getCartTotal()
                },
                onRemoveItem = { productId ->
                    cartManager.removeFromCart(productId)
                    cartItems = cartManager.getCartItems()
                    total = cartManager.getCartTotal()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Total y botón de checkout
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total:", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "$${String.format("%.0f", total)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navController.navigate("checkout")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Proceder al Pago")
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCartState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ShoppingCart,
            contentDescription = "Carrito vacío",
            modifier = Modifier.width(64.dp).height(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tu carrito está vacío",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Agrega algunos productos desde el catálogo",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun CartItemsList(
    cartItems: List<CartItem>,
    onQuantityChange: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cartItems) { item ->
            CartItemCard(
                item = item,
                onQuantityChange = onQuantityChange,
                onRemoveItem = onRemoveItem
            )
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityChange: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit
) {
    Card {
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
                        text = item.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$${String.format("%.0f", item.productPrice)} c/u",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Subtotal: $${String.format("%.0f", item.productPrice * item.quantity)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Botón eliminar
                IconButton(
                    onClick = { onRemoveItem(item.productId) }
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Selector de cantidad
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cantidad:", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (item.quantity > 1) {
                            onQuantityChange(item.productId, item.quantity - 1)
                        } else {
                            onRemoveItem(item.productId) // Eliminar si cantidad es 1
                        }
                    }
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = "Disminuir")
                }

                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = { onQuantityChange(item.productId, item.quantity + 1) }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Aumentar")
                }
            }
        }
    }
}