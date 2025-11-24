package com.example.creativasprint.admin.users

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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.creativasprint.model.User
import kotlinx.coroutines.launch

@Composable
fun AdminUsersScreen(navController: NavController) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // Cambiar a false ya que usamos datos locales

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ✅ DATOS TEMPORALES - Mientras configuras los endpoints en Xano
    val sampleUsers = listOf(
        User(
            id = 1,
            email = "cliente@creativasprint.com",
            name = "Cliente Demo",
            role = "client",
            isActive = true,
            phone = "+56 9 1234 5678",
            address = "Av. Principal 123, Santiago"
        ),
        User(
            id = 2,
            email = "admin@creativasprint.com",
            name = "Administrador",
            role = "admin",
            isActive = true,
            phone = "+56 9 8765 4321",
            address = "Oficina Central, CreativasPrint"
        ),
        User(
            id = 3,
            email = "maria.gonzalez@email.com",
            name = "María González",
            role = "client",
            isActive = true,
            phone = "+56 9 5555 1234",
            address = "Calle Secundaria 456, Providencia"
        ),
        User(
            id = 4,
            email = "usuario.bloqueado@email.com",
            name = "Usuario Bloqueado",
            role = "client",
            isActive = false, // ✅ Usuario bloqueado
            phone = "+56 9 9999 8888",
            address = "Sin dirección registrada"
        ),
        User(
            id = 5,
            email = "juan.perez@email.com",
            name = "Juan Pérez",
            role = "client",
            isActive = true,
            phone = "+56 9 7777 6666",
            address = "Plaza Central 789, Las Condes"
        )
    )

    LaunchedEffect(Unit) {
        // ✅ Usar datos temporales en lugar de la API
        users = sampleUsers
    }

    val filteredUsers = if (searchQuery.isEmpty()) {
        users
    } else {
        users.filter { user ->
            (user.name?.contains(searchQuery, ignoreCase = true) == true) ||
                    (user.email?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "📋 Vista de demostración - Usando datos de ejemplo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                "Para usar datos reales, configura los endpoints en Xano",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar usuarios...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    title = "Total",
                    value = users.size.toString(),
                    icon = Icons.Filled.Person
                )
                StatCard(
                    title = "Admins",
                    value = users.count { it.role == "admin" }.toString(),
                    icon = Icons.Filled.AdminPanelSettings
                )
                StatCard(
                    title = "Clientes",
                    value = users.count { it.role == "client" }.toString(),
                    icon = Icons.Filled.Person
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                            onUpdateRole = { userId, newRole ->
                                // ✅ Lógica temporal para cambiar rol
                                users = users.map {
                                    if (it.id == userId) {
                                        it.copy(role = newRole)
                                    } else {
                                        it
                                    }
                                }
                                scope.launch {
                                    snackbarHostState.showSnackbar("Rol cambiado a $newRole")
                                }
                            },
                            onToggleStatus = { userId, isActive ->
                                // ✅ Lógica temporal para activar/desactivar
                                users = users.map {
                                    if (it.id == userId) {
                                        it.copy(isActive = isActive)
                                    } else {
                                        it
                                    }
                                }
                                val action = if (isActive) "activado" else "desactivado"
                                scope.launch {
                                    snackbarHostState.showSnackbar("Usuario $action")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun AdminUserCard(
    user: User,
    onUpdateRole: (Int, String) -> Unit,
    onToggleStatus: (Int, Boolean) -> Unit
) {
    var showRoleMenu by remember { mutableStateOf(false) }

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
                        user.name ?: "Usuario sin nombre",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        user.email ?: "Email no disponible",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Botones de acción
                Column {
                    // Botón para cambiar rol
                    OutlinedButton(
                        onClick = { showRoleMenu = true },
                        modifier = Modifier.width(120.dp)
                    ) {
                        Text(user.role ?: "client")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón de estado
                    IconButton(
                        onClick = { onToggleStatus(user.id, !user.isActive) }
                    ) {
                        if (user.isActive) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = "Desactivar usuario",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                Icons.Filled.Block,
                                contentDescription = "Activar usuario",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Información adicional
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Estado:",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    if (user.isActive) "🟢 Activo" else "🔴 Inactivo",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (user.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            user.phone?.let { phone ->
                if (phone.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("📞 Teléfono:", style = MaterialTheme.typography.bodySmall)
                        Text(phone, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            user.address?.let { address ->
                if (address.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("📍 Dirección:", style = MaterialTheme.typography.bodySmall)
                        Text(address, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Nota para desarrollo
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "ID: ${user.id} • Datos de demostración",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    // Dialog para cambiar rol
    if (showRoleMenu) {
        AlertDialog(
            onDismissRequest = { showRoleMenu = false },
            title = { Text("Cambiar Rol del Usuario") },
            text = {
                Column {
                    Text("Selecciona el nuevo rol para:")
                    Text(
                        "${user.name ?: "Usuario"} (${user.email})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            onUpdateRole(user.id, "admin")
                            showRoleMenu = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("👑 Administrador")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onUpdateRole(user.id, "client")
                            showRoleMenu = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("👤 Cliente")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showRoleMenu = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}