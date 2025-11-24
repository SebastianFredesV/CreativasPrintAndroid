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
import com.example.creativasprint.model.User
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var shouldRegister by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // LaunchedEffect para manejar el registro
    if (shouldRegister) {
        LaunchedEffect(shouldRegister) {
            if (shouldRegister) {
                isLoading = true
                errorMessage = null

                // Validaciones
                if (password != confirmPassword) {
                    errorMessage = "Las contraseñas no coinciden"
                    isLoading = false
                    shouldRegister = false
                    return@LaunchedEffect
                }

                if (password.length < 6) {
                    errorMessage = "La contraseña debe tener al menos 6 caracteres"
                    isLoading = false
                    shouldRegister = false
                    return@LaunchedEffect
                }

                delay(1500) // Simular llamada API

                // Verificar si el email ya existe (simulación)
                val existingUsers = listOf(
                    "admin@creativasprint.com",
                    "cliente@creativasprint.com"
                )

                if (existingUsers.contains(email)) {
                    errorMessage = "Este email ya está registrado"
                    isLoading = false
                    shouldRegister = false
                    return@LaunchedEffect
                }

                // Crear nuevo usuario (siempre como cliente)
                val newUser = User(
                    id = (existingUsers.size + 1).toInt(),
                    email = email,
                    name = name,
                    role = SessionManager.ROLE_CLIENT
                )

                isLoading = false
                shouldRegister = false

                // Guardar sesión y redirigir
                sessionManager.saveUserSession(newUser)
                navController.navigate(Destinations.ClientMain.route) {
                    popUpTo(Destinations.Register.route) { inclusive = true }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Cuenta")

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = androidx.compose.ui.graphics.Color.Red)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                        shouldRegister = true
                    } else {
                        errorMessage = "Completa todos los campos"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            navController.navigate(Destinations.Login.route) {
                popUpTo(Destinations.Register.route) { inclusive = true }
            }
        }) {
            Text("¿Ya tienes cuenta? Inicia Sesión")
        }
    }
}