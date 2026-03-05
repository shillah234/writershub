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

@Composable
fun DashboardScreen(
    onLogoutClick: () -> Unit
) {
    val isActivated = SessionManager.isUserActivated()
    val user = SessionManager.currentUser

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Welcome Text with user name
            Text(
                text = "Welcome ${user?.name ?: "User"}!",
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Moving text/ticker
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "For assistance contact support@writershub.com",
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!isActivated) {
                // Show Activation Card prominently for non-activated users
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFC107) // Yellow
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "⚠️ Activation Required",
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "Pay KES 100 to access all features",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Wallet Balance Card (Orange) - Only show if activated
            if (isActivated) {
                WalletCard(
                    title = "Wallet Balance",
                    amount = "KES ${user?.walletBalance ?: 0.00}",
                    color = Color(0xFFFF9800) // Orange
                )
            }

            // Withdrawn Card (Green) - Only show if activated
            if (isActivated) {
                WalletCard(
                    title = "Total Withdrawn",
                    amount = "KES ${user?.totalWithdrawn ?: 0.00}",
                    color = Color(0xFF4CAF50) // Green
                )
            }

            // Total Earnings Card (Blue) - Only show if activated
            if (isActivated) {
                WalletCard(
                    title = "Total Earnings",
                    amount = "KES ${user?.totalEarnings ?: 0.00}",
                    color = Color(0xFF2196F3) // Blue
                )
            }

            // Activation Fee Card (Yellow) - Only show if NOT activated
            if (!isActivated) {
                WalletCard(
                    title = "Activation Fee",
                    amount = "KES 100",
                    color = Color(0xFFFFC107) // Yellow
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            Button(
                onClick = {
                    SessionManager.logout()
                    onLogoutClick()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}

@Composable
fun WalletCard(
    title: String,
    amount: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = amount,
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
        }
    }
}