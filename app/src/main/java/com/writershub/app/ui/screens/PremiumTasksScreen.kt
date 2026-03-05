package com.writershub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.writershub.app.data.repository.SessionManager
import com.writershub.app.data.repository.TaskRepository
import com.writershub.app.ui.components.TaskCard

@Composable
fun PremiumTasksScreen(
    onBackClick: () -> Unit,
    onTaskDetailClick: (String) -> Unit
) {
    val isActivated = SessionManager.isUserActivated()
    val premiumTasks = remember { TaskRepository.getPremiumTasks() }

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

        if (!isActivated) {
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
        }

        // Tasks List
        LazyColumn {
            items(premiumTasks.size) { index ->
                TaskCard(
                    task = premiumTasks[index],
                    onTaskClick = {
                        if (isActivated) {
                            onTaskDetailClick(premiumTasks[index].id)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}