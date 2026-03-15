package com.writershub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.writershub.app.data.model.Task  // ADD THIS IMPORT
import com.writershub.app.data.repository.TaskRepository
import com.writershub.app.data.repository.SessionManager
import com.writershub.app.ui.components.TaskCard
import com.writershub.app.ui.components.EmptyState
import kotlinx.coroutines.delay

@Composable
fun TasksScreen(
    onBackClick: () -> Unit,
    onTaskDetailClick: (String) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    val dailyTasks = remember { TaskRepository.getDailyTasks() }
    val isActivated = SessionManager.isUserActivated()
    val premiumTasks = remember { TaskRepository.getPremiumTasks() }

    LaunchedEffect(Unit) {
        delay(1500)
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
        } else {
            LazyColumn {
                // Daily Tasks
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

                // Premium Tasks
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
            }
        }
    }
}