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
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var shouldLogin by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // LaunchedEffect para manejar el login de manera correcta
    if (shouldLogin) {
        LaunchedEffect(shouldLogin) {
            if (shouldLogin) {
                isLoading = true
                errorMessage = null

                delay(1500) // Simular llamada API

                val user = when {
                    email == "admin@creativasprint.com" && password == "admin123" -> {
                        User("1", email, "Administrador", SessionManager.ROLE_ADMIN)
                    }
                    email == "cliente@creativasprint.com" && password == "cliente123" -> {
                        User("2", email, "Cliente Demo", SessionManager.ROLE_CLIENT)
                    }
                    else -> null
                }

                isLoading = false
                shouldLogin = false

                if (user != null) {
                    sessionManager.saveUserSession(user)
                    val destination = if (user.role == SessionManager.ROLE_ADMIN) {
                        Destinations.AdminMain.route
                    } else {
                        Destinations.ClientMain.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                } else {
                    errorMessage = "Credenciales incorrectas"
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
        Text("CreativasPrint")

        Spacer(modifier = Modifier.height(32.dp))

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
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        shouldLogin = true
                    } else {
                        errorMessage = "Completa todos los campos"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            navController.navigate(Destinations.Register.route)
        }) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}