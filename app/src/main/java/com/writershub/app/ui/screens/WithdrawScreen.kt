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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.platform.LocalContext
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.writershub.app.data.repository.SessionManager
import com.writershub.app.data.model.WithdrawalStatus
import com.writershub.app.ui.components.EmptyState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WithdrawScreen(
    onBackClick: () -> Unit,
    onWithdrawSuccess: () -> Unit
) {
    val user = SessionManager.currentUser
    var selectedTab by remember { mutableStateOf(0) } // 0 = Withdraw, 1 = History

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
                text = "Withdraw Funds",
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Row
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Withdraw") },
                icon = { Icon(Icons.Default.Info, null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("History") },
                icon = { Icon(Icons.Default.History, null) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content based on selected tab
        when (selectedTab) {
            0 -> WithdrawTab(onWithdrawSuccess)
            1 -> HistoryTab()
        }
    }
}

@Composable
fun WithdrawTab(
    onWithdrawSuccess: () -> Unit
) {
    val user = SessionManager.currentUser
    var amount by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf(user?.phone ?: "") }
    var showError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "KE"))
    val balance = user?.walletBalance ?: 0.0

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
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

                // Amount Field
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

                Spacer(modifier = Modifier.height(12.dp))

                // M-Pesa Phone Number Field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        showError = false
                        showSuccess = false
                    },
                    label = { Text("M-Pesa Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("07XXXXXXXX") },
                    enabled = !isProcessing,
                    supportingText = {
                        Text("Enter the phone number to receive funds")
                    }
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
                                text = "Amount: ${currencyFormat.format(amount.toDoubleOrNull() ?: 0.0)}",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "Phone: $phoneNumber",
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Status: PENDING - Waiting for processing",
                                fontSize = 14.sp,
                                color = Color(0xFFFF9800),
                                modifier = Modifier.padding(top = 4.dp)
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

                            val result = SessionManager.requestWithdrawal(withdrawAmount, phoneNumber)

                            when (result) {
                                "SUCCESS" -> {
                                    showSuccess = true
                                    showError = false
                                    amount = ""

                                    // Stay on screen to show success, then go back after 3 seconds
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
                                "INVALID_PHONE" -> {
                                    showError = true
                                    showSuccess = false
                                    errorMessage = "Invalid phone number. Use format: 07XXXXXXXX"
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
                        Text("Request Withdrawal")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Minimum withdrawal: KES 1000",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun HistoryTab() {
    val withdrawals = SessionManager.getWithdrawalHistory()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "KE"))
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Withdrawal History",
            fontSize = 18.sp,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (withdrawals.isEmpty()) {
            // 👇 UPDATED: Using EmptyState component
            EmptyState(
                icon = Icons.Default.History,
                title = "No Withdrawals Yet",
                message = "Your withdrawal history will appear here once you make your first withdrawal.",
                buttonText = null,
                onButtonClick = null,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn {
                items(withdrawals) { withdrawal ->
                    WithdrawalHistoryItem(
                        withdrawal = withdrawal,
                        currencyFormat = currencyFormat,
                        dateFormat = dateFormat
                    )
                }
            }
        }
    }
}

@Composable
fun WithdrawalHistoryItem(
    withdrawal: com.writershub.app.data.model.Withdrawal,
    currencyFormat: NumberFormat,
    dateFormat: SimpleDateFormat
) {
    val statusColor = when (withdrawal.status) {
        WithdrawalStatus.PENDING -> Color(0xFFFF9800) // Orange
        WithdrawalStatus.DISBURSED -> Color(0xFF4CAF50) // Green
        WithdrawalStatus.FAILED -> Color.Red
    }

    val statusText = when (withdrawal.status) {
        WithdrawalStatus.PENDING -> "PENDING"
        WithdrawalStatus.DISBURSED -> "DISBURSED"
        WithdrawalStatus.FAILED -> "FAILED"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currencyFormat.format(withdrawal.amount),
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = statusColor.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = statusColor,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Phone: ${withdrawal.phoneNumber}",
                fontSize = 14.sp
            )

            Text(
                text = "Requested: ${dateFormat.format(withdrawal.requestDate)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            if (withdrawal.status == WithdrawalStatus.DISBURSED && withdrawal.mpesaReference != null) {
                Text(
                    text = "M-Pesa Ref: ${withdrawal.mpesaReference}",
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}