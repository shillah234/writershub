package com.writershub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.writershub.app.data.repository.TaskRepository
import com.writershub.app.data.repository.SessionManager

@Composable
fun TaskDetailScreen(
    taskId: String,
    onBackClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    val task = TaskRepository.getTaskById(taskId)
    val isCompleted = SessionManager.isTaskCompleted(taskId)
    var showSuccessMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (task == null) {
        // Task not found
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Task not found")
            Button(onClick = onBackClick) {
                Text("Go Back")
            }
        }
        return
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
            Text(
                text = task.title,
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineMedium
            )

            Button(onClick = onBackClick) {
                Text("Back")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Task Details Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Icon and type
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.icon,
                        fontSize = 48.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Column {
                        Text(
                            text = task.type.name,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${task.difficulty.name} • ${task.timeInMinutes} min",
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Description",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = task.description,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Reward
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reward:",
                            fontSize = 18.sp
                        )
                        Text(
                            text = "KES ${task.reward}",
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Success Message
                if (showSuccessMessage) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50) // Green
                        )
                    ) {
                        Text(
                            text = "✅ Task Completed! KES ${task.reward} added to your wallet",
                            modifier = Modifier.padding(16.dp),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Complete Button
                Button(
                    onClick = {
                        if (!isCompleted) {
                            // Add reward to wallet
                            SessionManager.addTaskReward(task.reward, task.id)
                            showSuccessMessage = true

                            // Close screen after 2 seconds
                            scope.launch {
                                delay(2000)
                                onCompleteClick()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted)
                            Color.Gray
                        else
                            MaterialTheme.colorScheme.primary
                    ),
                    enabled = !isCompleted
                ) {
                    Text(
                        text = if (isCompleted) "Already Completed" else "Complete Task",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}