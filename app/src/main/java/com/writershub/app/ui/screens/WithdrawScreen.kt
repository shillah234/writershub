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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun WithdrawScreen(
    onBackClick: () -> Unit,
    onWithdrawSuccess: () -> Unit
) {
    val user = SessionManager.currentUser
    var amount by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "KE"))
    val balance = user?.walletBalance ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Withdraw Funds",
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Balance Card
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
                    text = "Available Balance",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = currencyFormat.format(balance),
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Withdrawal Form
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Withdraw to M-Pesa",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        showError = false
                        showSuccess = false
                    },
                    label = { Text("Amount (Min KES 1000)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Enter amount") },
                    enabled = !isProcessing
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Phone: ${user?.phone ?: "07XXXXXXXX"}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Error Message
                if (showError) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Success Message
                if (showSuccess) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "✅ Withdrawal Request Submitted!",
                                fontSize = 16.sp,
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                text = "Your withdrawal of ${currencyFormat.format(amount.toDoubleOrNull() ?: 0.0)} is being processed.",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "Please wait for M-Pesa confirmation.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Withdraw Button
                Button(
                    onClick = {
                        val withdrawAmount = amount.toDoubleOrNull() ?: 0.0
                        isProcessing = true

                        // Simulate processing delay
                        scope.launch {
                            delay(1000)

                            val result = SessionManager.withdrawMoney(withdrawAmount)

                            when (result) {
                                "SUCCESS" -> {
                                    showSuccess = true
                                    showError = false
                                    amount = ""

                                    // Go back after 3 seconds
                                    delay(3000)
                                    onWithdrawSuccess()
                                }
                                "MINIMUM_FAILED" -> {
                                    showError = true
                                    showSuccess = false
                                    errorMessage = "Minimum withdrawal amount is KES 1000"
                                }
                                "BALANCE_FAILED" -> {
                                    showError = true
                                    showSuccess = false
                                    errorMessage = "Insufficient balance"
                                }
                            }

                            isProcessing = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = balance >= 1000 && !isProcessing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                ) {
                    if (isProcessing) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing...")
                        }
                    } else {
                        Text("Withdraw to M-Pesa")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Minimum withdrawal notice
                Text(
                    text = "Minimum withdrawal: KES 1000",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}