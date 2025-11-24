package com.example.creativasprint.admin.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.creativasprint.model.User

@Composable
fun AdminUsersScreen(navController: NavController) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    // Datos de ejemplo
    val sampleUsers = listOf(
        User(
            id = 1,
            email = "admin@creativasprint.com",
            name = "Administrador",
            role = "admin",
            isActive = true
        ),
        User(
            id = 2,
            email = "cliente@creativasprint.com",
            name = "Cliente Demo",
            role = "client",
            isActive = true
        ),
        User(
            id = 3,
            email = "juan.perez@email.com",
            name = "Juan Pérez",
            role = "client",
            isActive = false
        )
    )

    users = sampleUsers

    val filteredUsers = if (searchQuery.isEmpty()) {
        users
    } else {
        users.filter { user ->
            user.name.contains(searchQuery, ignoreCase = true) ||
                    user.email.contains(searchQuery, ignoreCase = true)
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
                "Gestión de Usuarios",
                style = MaterialTheme.typography.headlineMedium
            )

            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar usuarios...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                singleLine = true
            )

            if (filteredUsers.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No se encontraron usuarios")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredUsers) { user ->
                        AdminUserCard(
                            user = user,
                            onToggleStatus = { /* Lógica para bloquear/desbloquear */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUserCard(
    user: User,
    onToggleStatus: (User) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(user.email)
                Text(
                    "Rol: ${if (user.role == "admin") "Administrador" else "Cliente"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Estado: ${if (user.isActive) "Activo" else "Bloqueado"}",
                    color = if (user.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(
                onClick = { onToggleStatus(user) }
            ) {
                Icon(
                    if (user.isActive) Icons.Filled.Block else Icons.Filled.CheckCircle,
                    contentDescription = if (user.isActive) "Bloquear" else "Desbloquear"
                )
            }
        }
    }
}