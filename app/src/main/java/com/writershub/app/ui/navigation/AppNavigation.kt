package com.writershub.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.writershub.app.ui.screens.LoginScreen
import com.writershub.app.ui.screens.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"  // Start with Login screen
    ) {
        composable("login") {
            LoginScreen(
                onLoginClick = {
                    // When login button clicked, go to dashboard (we'll create this next)
                    // For now, just print
                    println("Login clicked - will navigate to dashboard")
                },
                onRegisterClick = {
                    // When "Register" link clicked, go to register screen
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterClick = {
                    // When register button clicked, go back to login
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginClick = {
                    // When "Login" link clicked, go back to login
                    navController.navigate("login")
                }
            )
        }
    }
}