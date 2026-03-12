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
import androidx.compose.material.icons.filled.Info
import kotlinx.coroutines.launch
import com.writershub.app.data.mpesa.MpesaManager
import com.writershub.app.data.mpesa.MpesaResult
import com.writershub.app.data.repository.SessionManager

@Composable
fun ActivationScreen(
    onActivateClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var paymentStatus by remember { mutableStateOf<MpesaResult?>(null) }
    var checkoutRequestId by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Text(
            text = "Activate Your Account",
            fontSize = 28.sp,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Info Card
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

        Spacer(modifier = Modifier.height(24.dp))

        // Phone Number Input
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                paymentStatus = null
            },
            label = { Text("M-Pesa Phone Number") },
            placeholder = { Text("07XXXXXXXX") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            supportingText = {
                Text("Enter the M-Pesa number you'll pay from")
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Status Message Display
        paymentStatus?.let { result ->
            when (result) {
                is MpesaResult.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "✅ STK Push Sent!",
                                    color = Color(0xFF4CAF50),
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                                Text(
                                    text = result.message,
                                    fontSize = 12.sp,
                                    color = Color(0xFF4CAF50)
                                )
                                Text(
                                    text = "Check your phone and enter PIN to complete payment",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                }
                is MpesaResult.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "❌ ${result.message}",
                                color = Color.Red
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Activate Button
        Button(
            onClick = {
                if (phoneNumber.isBlank()) {
                    paymentStatus = MpesaResult.Error("Please enter your M-Pesa phone number")
                    return@Button
                }

                if (!phoneNumber.matches(Regex("^(07|01)\\d{8}$"))) {
                    paymentStatus = MpesaResult.Error("Invalid phone number. Use format: 07XXXXXXXX")
                    return@Button
                }

                isLoading = true
                paymentStatus = null

                MpesaManager.initiateActivationPayment(phoneNumber, 100) { result ->
                    scope.launch {
                        isLoading = false
                        paymentStatus = result

                        if (result is MpesaResult.Success) {
                            checkoutRequestId = result.checkoutRequestId
                            // Note: We don't activate immediately - wait for callback
                            // The account will be activated when we receive confirmation
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Pay KES 100 via M-Pesa")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Manual Check Button (for testing)
        if (checkoutRequestId != null) {
            OutlinedButton(
                onClick = {
                    // For now, just simulate activation for testing
                    SessionManager.activateAccount()
                    onActivateClick()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("I've Completed Payment (Test Only)")
            }
            Text(
                text = "This button is for testing only. In production, payment is automatic.",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Skip Link
        TextButton(
            onClick = onSkipClick,
            enabled = !isLoading
        ) {
            Text("Skip for now")
        }

        // Info Text
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "You'll receive an STK push on your phone. Enter your PIN to complete payment.",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}