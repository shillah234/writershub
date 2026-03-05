package com.writershub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.writershub.app.data.repository.TaskRepository
import com.writershub.app.ui.components.TaskCard

@Composable
fun ShortVideosScreen(
    onBackClick: () -> Unit,
    onTaskDetailClick: (String) -> Unit
) {
    val videoTasks = remember { TaskRepository.getVideoTasks() }

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
                    text = "Short Videos",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Watch and earn",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(onClick = onBackClick) {
                Text("Back")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Videos List
        LazyColumn {
            items(videoTasks.size) { index ->
                TaskCard(
                    task = videoTasks[index],
                    onTaskClick = { onTaskDetailClick(videoTasks[index].id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}