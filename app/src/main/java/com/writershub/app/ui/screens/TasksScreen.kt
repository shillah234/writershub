package com.writershub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Task
import com.writershub.app.data.model.Task
import com.writershub.app.data.repository.TaskRepository
import com.writershub.app.data.repository.SessionManager
import com.writershub.app.ui.components.TaskCard
import com.writershub.app.ui.components.EmptyState
import androidx.compose.foundation.lazy.items
@Composable
fun TasksScreen(
    onBackClick: () -> Unit,
    onTaskDetailClick: (String) -> Unit
) {
    var dailyTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var premiumTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val isActivated = SessionManager.isUserActivated()

    // Load tasks from Firestore
    LaunchedEffect(Unit) {
        isLoading = true
        dailyTasks = TaskRepository.getDailyTasks()
        premiumTasks = TaskRepository.getPremiumTasks()
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Available Tasks",
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            // Show loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (dailyTasks.isEmpty() && premiumTasks.isEmpty()) {
            // Show empty state when no tasks
            EmptyState(
                icon = Icons.Default.Task,
                title = "No Tasks Available",
                message = "Check back later for new tasks!",
                buttonText = null,
                onButtonClick = null
            )
        } else {
            LazyColumn {
                // Daily Tasks Section
                if (dailyTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "Daily Tasks",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(dailyTasks) { task ->
                        TaskCard(
                            task = task,
                            onTaskClick = { onTaskDetailClick(task.id) },
                            isPremium = false
                        )
                    }
                }

                // Premium Tasks Section
                if (isActivated && premiumTasks.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Premium Tasks",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(premiumTasks) { task ->
                        TaskCard(
                            task = task,
                            onTaskClick = { onTaskDetailClick(task.id) },
                            isPremium = true
                        )
                    }
                }

                // Message for non-activated users
                if (!isActivated && premiumTasks.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = "🔒 Premium Tasks Locked - Activate your account to access higher earnings!",
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}