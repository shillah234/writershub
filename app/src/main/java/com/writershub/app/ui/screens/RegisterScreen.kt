package com.writershub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import kotlinx.coroutines.launch
import com.writershub.app.MainActivity
import com.writershub.app.data.repository.SessionManager

@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Get referral code from deep link if available
    val initialReferralCode = MainActivity.deepLinkReferralCode ?: ""
    var referralCode by remember { mutableStateOf(initialReferralCode) }
    var showReferralInfo by remember { mutableStateOf(initialReferralCode.isNotBlank()) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            fontSize = 28.sp,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Show referral info banner if opened from link
        if (showReferralInfo) {
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
                        Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "🎁 You were referred!",
                            fontSize = 14.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "Code: $referralCode",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // First Name Field
        OutlinedTextField(
            value = firstName,
            onValueChange = {
                firstName = it
                errorMessage = ""
            },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Last Name Field
        OutlinedTextField(
            value = lastName,
            onValueChange = {
                lastName = it
                errorMessage = ""
            },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Username Field (NEW)
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it.lowercase().replace(" ", "") // Auto lowercase, no spaces
                errorMessage = ""
            },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            placeholder = { Text("john76") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = ""
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Field
        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
                errorMessage = ""
            },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            placeholder = { Text("07XXXXXXXX") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Referral Code Field (auto-filled if from deep link)
        OutlinedTextField(
            value = referralCode,
            onValueChange = {
                if (!showReferralInfo) {
                    referralCode = it.uppercase()
                    errorMessage = ""
                }
            },
            label = { Text("Referral Code (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading && !showReferralInfo,
            placeholder = { Text("e.g., john7612") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password Field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = ""
            },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Error Message
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register Button
        Button(
            onClick = {
                // Validation
                if (firstName.isBlank() || lastName.isBlank() || username.isBlank() ||
                    email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    errorMessage = "Please fill all fields"
                    return@Button
                }

                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                    return@Button
                }

                if (password.length < 6) {
                    errorMessage = "Password must be at least 6 characters"
                    return@Button
                }

                if (!phone.matches(Regex("^(07|01)\\d{8}$"))) {
                    errorMessage = "Invalid phone number. Use 07XXXXXXXX"
                    return@Button
                }

                // Username validation (letters, numbers, underscore only)
                if (!username.matches(Regex("^[a-z0-9_]{3,20}$"))) {
                    errorMessage = "Username must be 3-20 chars (lowercase, numbers, _ only)"
                    return@Button
                }

                // Referral code validation (optional)
                if (referralCode.isNotBlank() && !referralCode.matches(Regex("^[a-z0-9]{6,}$"))) {
                    errorMessage = "Invalid referral code format"
                    return@Button
                }

                isLoading = true
                scope.launch {
                    val code = if (referralCode.isBlank()) null else referralCode
                    val result = SessionManager.register(
                        firstName = firstName,
                        lastName = lastName,
                        username = username.lowercase(),
                        email = email,
                        phone = phone,
                        password = password,
                        referralCode = code
                    )
                    isLoading = false

                    if (result.isSuccess) {
                        MainActivity.deepLinkReferralCode = null
                        onRegisterClick()
                    } else {
                        errorMessage = result.exceptionOrNull()?.message ?: "Registration failed"
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
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Login Link
        TextButton(
            onClick = onLoginClick,
            enabled = !isLoading
        ) {
            Text("Already have an account? Login")
        }

        // Info about referral bonus
        if (!isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = if (showReferralInfo) {
                        "🎁 You'll get KES 20 after completing your first task!"
                    } else {
                        "🎁 Referral Bonus: You get KES 20 when someone uses your code!\n" +
                                "Enter a friend's code above to help them earn too."
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}