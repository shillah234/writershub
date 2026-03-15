package com.writershub.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.writershub.app.data.model.Task

@Composable
fun TaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    isPremium: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPremium)
                Color(0xFFFFD700).copy(alpha = 0.2f)
            else
                MaterialTheme.colorScheme.surface
        ),
        onClick = onTaskClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Task Icon
            Text(
                text = task.icon,
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 16.dp)
            )

            // Task Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = task.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Reward
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "KES ${task.reward}",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                if (isPremium) {
                    Text(
                        text = "PREMIUM",
                        fontSize = 12.sp,
                        color = Color(0xFFB8860B)
                    )
                }
            }
        }
    }
}