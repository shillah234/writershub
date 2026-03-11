package com.writershub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.writershub.app.data.repository.SessionManager
import com.writershub.app.data.model.User

// Helper function to get user's full name
fun getUserFullName(user: User?): String {
    return if (user != null) {
        "${user.firstName} ${user.lastName}".trim().ifEmpty { "User" }
    } else {
        "User"
    }
}

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val user = SessionManager.currentUser
    val fullName = getUserFullName(user)

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
                text = "Settings",
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
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

                // UPDATED: Using firstName and lastName
                SettingItem("Full Name", fullName)
                Divider()
                SettingItem("Username", user?.username ?: "Not set")
                Divider()
                SettingItem("Email", user?.email ?: "email@example.com")
                Divider()
                SettingItem("Phone", user?.phone ?: "07XXXXXXXX")
                Divider()
                SettingItem("Account Status", if (user?.isActivated == true) "Activated" else "Not Activated")
                Divider()
                SettingItem("Referral Code", user?.referralCode ?: "Not set")
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

        Spacer(modifier = Modifier.height(16.dp))

        // Referral Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF9C27B0).copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🎁 Your Referral Code",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF9C27B0)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = user?.referralCode ?: "Not available",
                    fontSize = 24.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color(0xFF9C27B0),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Share this code with friends to earn KES 20 each!",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
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
            fontSize = 16.sp,
            fontWeight = if (label == "Referral Code") androidx.compose.ui.text.font.FontWeight.Bold else null,
            color = if (label == "Referral Code") Color(0xFF9C27B0) else MaterialTheme.colorScheme.onSurface
        )
    }
}