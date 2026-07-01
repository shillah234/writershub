package com.writershub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.writershub.app.data.model.Task
import com.writershub.app.data.repository.SessionManager
import com.writershub.app.data.repository.TaskRepository
import com.writershub.app.ui.components.TaskCard

@Composable
fun PremiumTasksScreen(
    onBackClick: () -> Unit,
    onTaskDetailClick: (String) -> Unit
) {
    val isActivated = SessionManager.isUserActivated()
    var premiumTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load premium tasks from Firestore
    LaunchedEffect(Unit) {
        isLoading = true
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Premium Tasks",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Higher earnings for activated users",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(onClick = onBackClick) {
                Text("Back")
            }
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
        } else if (!isActivated) {
            // Show activation message
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFC107)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🔒 Premium Tasks Locked",
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Activate your account (KES 100) to access premium tasks with higher earnings!",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else if (premiumTasks.isEmpty()) {
            // Show empty state when no premium tasks
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⭐ No Premium Tasks Available",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Check back later for new premium tasks!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            // Tasks List
            LazyColumn {
                items(premiumTasks) { task ->
                    TaskCard(
                        task = task,
                        onTaskClick = {
                            if (isActivated) {
                                onTaskDetailClick(task.id)
                            }
                        },
                        isPremium = true
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}