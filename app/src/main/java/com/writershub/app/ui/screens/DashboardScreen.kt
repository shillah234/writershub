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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onTasksClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDailyTasksClick: () -> Unit,
    onShortVideosClick: () -> Unit,
    onPremiumTasksClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val isActivated = SessionManager.isUserActivated()
    val user = SessionManager.currentUser

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(16.dp)
                ) {
                    Column {
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
                    }
                }

                Divider()

                // Navigation Items
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Daily Tasks") },
                    label = { Text("Daily Tasks") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onDailyTasksClick()
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Short Videos") },
                    label = { Text("Short Videos") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onShortVideosClick()
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Premium Tasks") },
                    label = {
                        if (isActivated) {
                            Text("Premium Tasks")
                        } else {
                            Text("Premium Tasks (Locked)", color = Color.Gray)
                        }
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (isActivated) {
                            onPremiumTasksClick()
                        }
                        // If not activated, do nothing (just close drawer)
                    }
                )

                Divider()

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onSettingsClick()
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
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
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
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
                    Text(
                        text = "Welcome ${user?.name ?: "User"}!",
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFC107)
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

                    if (isActivated) {
                        WalletCard(
                            title = "Wallet Balance",
                            amount = "KES ${user?.walletBalance ?: 0.00}",
                            color = Color(0xFFFF9800)
                        )

                        WalletCard(
                            title = "Total Withdrawn",
                            amount = "KES ${user?.totalWithdrawn ?: 0.00}",
                            color = Color(0xFF4CAF50)
                        )

                        WalletCard(
                            title = "Total Earnings",
                            amount = "KES ${user?.totalEarnings ?: 0.00}",
                            color = Color(0xFF2196F3)
                        )
                    }

                    if (!isActivated) {
                        WalletCard(
                            title = "Activation Fee",
                            amount = "KES 100",
                            color = Color(0xFFFFC107)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onTasksClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("View Available Tasks")
                    }
                }
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