package com.writershub.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.writershub.app.ui.screens.LoginScreen
import com.writershub.app.ui.screens.RegisterScreen
import com.writershub.app.ui.screens.ActivationScreen
import com.writershub.app.ui.screens.DashboardScreen
import com.writershub.app.ui.screens.TasksScreen
import com.writershub.app.ui.screens.ShortVideosScreen
import com.writershub.app.ui.screens.PremiumTasksScreen
import com.writershub.app.ui.screens.SettingsScreen
import com.writershub.app.ui.screens.TaskDetailScreen
import com.writershub.app.ui.screens.WithdrawScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Login Screen
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

        // Register Screen
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

        // Activation Screen
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

        // Dashboard Screen
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
                },
                onWithdrawClick = {
                    navController.navigate("withdraw")
                }
            )
        }

        // Tasks Screen (Daily Tasks)
        composable("tasks") {
            TasksScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onTaskDetailClick = { taskId ->
                    navController.navigate("task_detail/$taskId")
                }
            )
        }

        // Daily Tasks
        composable("daily_tasks") {
            TasksScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onTaskDetailClick = { taskId ->
                    navController.navigate("task_detail/$taskId")
                }
            )
        }

        // Short Videos Screen
        composable("short_videos") {
            ShortVideosScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onTaskDetailClick = { taskId ->
                    navController.navigate("task_detail/$taskId")
                }
            )
        }

        // Premium Tasks Screen
        composable("premium_tasks") {
            PremiumTasksScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onTaskDetailClick = { taskId ->
                    navController.navigate("task_detail/$taskId")
                }
            )
        }

        // Settings Screen
        composable("settings") {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // WITHDRAW SCREEN
        composable("withdraw") {
            WithdrawScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onWithdrawSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // Task Detail Screen
        composable(
            route = "task_detail/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskDetailScreen(
                taskId = taskId,
                onBackClick = {
                    navController.popBackStack()
                },
                onCompleteClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}