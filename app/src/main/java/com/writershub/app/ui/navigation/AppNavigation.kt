package com.writershub.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.writershub.app.ui.screens.LoginScreen
import com.writershub.app.ui.screens.RegisterScreen
import com.writershub.app.ui.screens.ActivationScreen
import com.writershub.app.ui.screens.DashboardScreen
import com.writershub.app.ui.screens.TasksScreen
import com.writershub.app.ui.screens.PlaceholderScreen

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
                    navController.navigate("dashboard")
                },
                onSkipClick = {
                    navController.navigate("dashboard")
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                onTasksClick = {
                    navController.navigate("tasks")
                },
                onLogoutClick = {
                    navController.popBackStack()
                    navController.navigate("login")
                },
                onDailyTasksClick = {
                    navController.navigate("daily_tasks")
                },
                onShortVideosClick = {
                    navController.navigate("short_videos")
                },
                onPremiumTasksClick = {
                    navController.navigate("premium_tasks")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }

        composable("tasks") {
            TasksScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("daily_tasks") {
            PlaceholderScreen(
                title = "Daily Tasks",
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("short_videos") {
            PlaceholderScreen(
                title = "Short Videos",
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("premium_tasks") {
            PlaceholderScreen(
                title = "Premium Tasks",
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("settings") {
            PlaceholderScreen(
                title = "Settings",
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}