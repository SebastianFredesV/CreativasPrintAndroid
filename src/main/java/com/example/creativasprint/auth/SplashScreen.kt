package com.example.creativasprint.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.creativasprint.destinations.Destinations
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    LaunchedEffect(Unit) {
        delay(2000) // 2 segundos de splash

        if (sessionManager.isLoggedIn()) {
            // Redirigir según el rol
            val destination = if (sessionManager.isAdmin()) {
                Destinations.AdminMain.route
            } else {
                Destinations.ClientMain.route
            }
            navController.navigate(destination) {
                popUpTo(Destinations.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Destinations.Login.route) {
                popUpTo(Destinations.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("CreativasPrint")
    }
}