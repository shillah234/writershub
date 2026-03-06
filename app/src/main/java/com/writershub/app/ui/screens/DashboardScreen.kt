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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import kotlinx.coroutines.launch
import com.writershub.app.data.repository.SessionManager
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onTasksClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDailyTasksClick: () -> Unit,
    onShortVideosClick: () -> Unit,
    onPremiumTasksClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onWithdrawClick: () -> Unit
) {
    val isActivated = SessionManager.isUserActivated()
    val user = SessionManager.currentUser

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "KE"))

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "WritersHub",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = user?.name ?: "User",
                        fontSize = 18.sp
                    )
                    Text(
                        text = user?.email ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Balance: ${currencyFormat.format(user?.walletBalance ?: 0.00)}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Divider()

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Daily Tasks") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onDailyTasksClick()
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    label = { Text("Short Videos") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onShortVideosClick()
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = {
                        if (isActivated) Text("Premium Tasks")
                        else Text("Premium Tasks (Locked)", color = Color.Gray)
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (isActivated) onPremiumTasksClick()
                    }
                )

                Divider()

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onSettingsClick()
                    }
                )

                // Withdraw with Money icon
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Money, contentDescription = null) },
                    label = { Text("Withdraw Funds") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onWithdrawClick()
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                    label = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        SessionManager.logout()
                        onLogoutClick()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    },
                    actions = {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Text("${SessionManager.getCompletedTasksCount()}")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Column {
                        // Welcome Text
                        Text(
                            text = "Welcome ${user?.name ?: "User"}!",
                            fontSize = 24.sp,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Support Banner
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "📢 For assistance contact support@writershub.com",
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Activation Required Card (if not activated)
                        if (!isActivated) {
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
                                        text = "Pay KES 100 to access premium tasks and higher earnings",
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Stats Summary Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Tasks Completed
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${SessionManager.getCompletedTasksCount()}",
                                        fontSize = 20.sp,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                    )
                                    Text(
                                        text = "Tasks Done",
                                        fontSize = 12.sp
                                    )
                                }

                                // Activation Status
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (isActivated) "✅" else "🔒",
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        text = if (isActivated) "Activated" else "Locked",
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Wallet Cards Section Title
                        Text(
                            text = "Your Wallet",
                            fontSize = 20.sp,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // WALLET BALANCE CARD (Orange)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFF9800) // Orange
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "💰 Wallet Balance", color = Color.White)
                                Text(
                                    text = currencyFormat.format(user?.walletBalance ?: 0.00),
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                            }
                        }

                        // TOTAL WITHDRAWN CARD (Green) - Only if activated
                        if (isActivated) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF4CAF50) // Green
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "📤 Total Withdrawn", color = Color.White)
                                    Text(
                                        text = currencyFormat.format(user?.totalWithdrawn ?: 0.00),
                                        color = Color.White,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        }

                        // TOTAL EARNINGS CARD (Blue) - Only if activated
                        if (isActivated) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF2196F3) // Blue
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "📈 Total Earnings", color = Color.White)
                                    Text(
                                        text = currencyFormat.format(user?.totalEarnings ?: 0.00),
                                        color = Color.White,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        }

                        // ACTIVATION FEE CARD (Yellow) - Only if NOT activated
                        if (!isActivated) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFC107) // Yellow
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "🔑 Activation Fee", color = Color.Black)
                                    Text(text = "KES 100", color = Color.Black, fontSize = 20.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Quick Actions Section
                        Text(
                            text = "Quick Actions",
                            fontSize = 20.sp,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tasks Button
                        Button(
                            onClick = onTasksClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(text = "📋 View Available Tasks", fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Withdraw Button (only if balance > 0)
                        if ((user?.walletBalance ?: 0.0) > 0) {
                            Button(
                                onClick = onWithdrawClick,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50) // Green
                                )
                            ) {
                                Text(text = "💸 Withdraw Earnings", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}