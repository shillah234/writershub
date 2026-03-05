package com.writershub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActivationScreen(
    onActivateClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Activate Your Account",
            fontSize = 28.sp,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "KES 100 Activation Fee",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "This one-time fee helps us:\n" +
                            "• Verify your identity\n" +
                            "• Prevent spam accounts\n" +
                            "• Provide quality tasks",
                    fontSize = 14.sp
                )
            }
        }
        Button(
            onClick = {
                // Mark user as activated
                com.writershub.app.data.repository.SessionManager.activateAccount()
                // Navigate
                onActivateClick()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pay KES 100 via M-Pesa")
        }
        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onSkipClick
        ) {
            Text("Skip for now")
        }
    }
}