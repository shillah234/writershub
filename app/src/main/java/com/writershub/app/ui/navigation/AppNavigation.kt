package com.writershub.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.writershub.app.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginClick = {
                    // After login, go to activation (not dashboard directly)
                    navController.navigate("activation")
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterClick = {
                    // After registration, go to activation
                    navController.navigate("activation")
                },
                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }

        composable("activation") {
            ActivationScreen(
                onActivateClick = {
                    // After activation, go to dashboard with all features
                    navController.navigate("dashboard")
                },
                onSkipClick = {
                    // If they skip, go to dashboard but show activation card
                    navController.navigate("dashboard")
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                onLogoutClick = {
                    navController.popBackStack()
                    navController.navigate("login")
                }
            )
        }
    }
}