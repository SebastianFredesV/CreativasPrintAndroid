package com.example.creativasprint.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.creativasprint.destinations.Destinations
import com.example.creativasprint.network.ApiClient
import com.example.creativasprint.network.requests.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("CreativasPrint")

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null // Limpiar error cuando el usuario escriba
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null // Limpiar error cuando el usuario escriba
            },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            isError = errorMessage != null
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        performRealLogin(
                            email = email,
                            password = password,
                            sessionManager = sessionManager,
                            navController = navController,
                            onLoading = { isLoading = it },
                            onError = { errorMessage = it }
                        )
                    } else {
                        errorMessage = "Completa todos los campos"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                navController.navigate(Destinations.Register.route)
            },
            enabled = !isLoading
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}

// Función para realizar login real con la API
private fun performRealLogin(
    email: String,
    password: String,
    sessionManager: SessionManager,
    navController: NavController,
    onLoading: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val loginRequest = LoginRequest(email, password)
            val response = ApiClient.authService.login(loginRequest)

            withContext(Dispatchers.Main) {
                onLoading(false)

                if (response.isSuccessful) {
                    val authResponse = response.body()

                    if (authResponse?.success == true && authResponse.user != null) {
                        // ✅ Login exitoso
                        sessionManager.saveUserSession(authResponse.user, authResponse.token)

                        val destination = if (authResponse.user.role == SessionManager.ROLE_ADMIN) {
                            Destinations.AdminMain.route
                        } else {
                            Destinations.ClientMain.route
                        }

                        navController.navigate(destination) {
                            popUpTo(Destinations.Login.route) { inclusive = true }
                        }
                    } else {
                        // ❌ Login fallido
                        onError(authResponse?.message ?: "Credenciales incorrectas")
                    }
                } else {
                    // ❌ Error de servidor
                    val errorMsg = when (response.code()) {
                        401 -> "Credenciales incorrectas"
                        404 -> "Usuario no encontrado"
                        500 -> "Error del servidor"
                        else -> "Error en el login: ${response.code()}"
                    }
                    onError(errorMsg)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onLoading(false)
                // ❌ Error de conexión - Manejamos específicamente el error de permisos
                val errorMsg = when {
                    e.message?.contains("Permission denied") == true ->
                        "Error de permisos: La app no tiene acceso a internet"
                    e.message?.contains("Unable to resolve host") == true ->
                        "Error de conexión: No se puede conectar al servidor"
                    else -> "Error de conexión: ${e.message}"
                }
                onError(errorMsg)
            }
        }
    }
}