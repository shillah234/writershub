package com.writershub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.writershub.app.data.repository.SessionManager

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val user = SessionManager.currentUser

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
                text = "Settings",
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineMedium
            )

            Button(onClick = onBackClick) {
                Text("Back")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Profile Information",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                SettingItem("Name", user?.name ?: "User")
                Divider()
                SettingItem("Email", user?.email ?: "email@example.com")
                Divider()
                SettingItem("Phone", user?.phone ?: "07XXXXXXXX")
                Divider()
                SettingItem("Account Status", if (user?.isActivated == true) "Activated" else "Not Activated")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Account Actions
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Account Actions",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { /* Change password */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change Password")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { /* Update phone number */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Phone Number")
                }

                if (user?.isActivated == false) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { /* Go to activation */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Activate Account (KES 100)")
                    }
                }
            }
        }
    }
}

@Composable
fun SettingItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 16.sp
        )
    }
}