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
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import kotlinx.coroutines.launch
import com.writershub.app.data.repository.SessionManager
import com.writershub.app.data.model.User
import com.writershub.app.data.model.Announcement
import com.writershub.app.data.repository.AnnouncementRepository
import com.writershub.app.ui.components.GradientWalletCard
import com.writershub.app.ui.components.AnimatedButton
import com.writershub.app.ui.theme.*
import java.text.NumberFormat
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date
import android.util.Log

// Helper function to get user's full name
fun getUserDisplayName(user: User?): String {
    return if (user != null) {
        "${user.firstName} ${user.lastName}".trim().ifEmpty { "User" }
    } else {
        "User"
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DashboardScreen(
    onTasksClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDailyTasksClick: () -> Unit,
    onShortVideosClick: () -> Unit,
    onPremiumTasksClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onTransactionHistoryClick: () -> Unit,
    onReferralClick: () -> Unit
) {
    val isActivated = SessionManager.isUserActivated()
    val user = SessionManager.currentUser

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "KE"))

    var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }
    var isLoadingAnnouncements by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoadingAnnouncements = true
        try {
            announcements = AnnouncementRepository.getActiveAnnouncements()
        } catch (e: Exception) {
            Log.e("DashboardScreen", "Error loading announcements: ${e.message}")
        }
        isLoadingAnnouncements = false
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Primary, PrimaryDark)
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "WritersHub",
                            fontSize = 24.sp,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        val fullName = getUserDisplayName(user)
                        Text(
                            text = fullName,
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = user?.email ?: "",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = "Balance: ${currencyFormat.format(user?.walletBalance ?: 0.00)}",
                                modifier = Modifier.padding(8.dp),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Divider()

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Primary) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null, tint = Primary) },
                    label = { Text("Daily Tasks") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onDailyTasksClick()
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Primary) },
                    label = { Text("Short Videos") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onShortVideosClick()
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = null, tint = Primary) },
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
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = WalletPurple) },
                    label = {
                        Row {
                            Text("Refer & Earn")
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge(
                                containerColor = WalletGreen
                            ) {
                                Text("KES ${SessionManager.getReferralEarnings().toInt()}")
                            }
                        }
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onReferralClick()
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.History, contentDescription = null, tint = WalletBlue) },
                    label = { Text("Transaction History") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onTransactionHistoryClick()
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Money, contentDescription = null, tint = WalletGreen) },
                    label = { Text("Withdraw Funds") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onWithdrawClick()
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Gray) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onSettingsClick()
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Error) },
                    label = { Text("Logout", color = Error) },
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
                    title = { Text("Dashboard", color = MaterialTheme.colorScheme.onPrimary) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
                    actions = {
                        Badge(containerColor = WalletYellow) { Text("${SessionManager.getCompletedTasksCount()}") }
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
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                            initialOffsetY = { it / 4 },
                            animationSpec = tween(500)
                        )
                    ) {
                        Column {
                            val fullName = getUserDisplayName(user)
                            Text(
                                text = "Welcome ${fullName}!",
                                fontSize = 24.sp,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Secondary.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Secondary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "For assistance contact support@writershub.com", fontSize = 14.sp, color = Secondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            if (!isActivated) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = WalletYellow.copy(alpha = 0.1f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = WalletYellow)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = "⚠️ Activation Required", fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = WalletYellow)
                                            Text(text = "Pay KES 100 to access all features", fontSize = 14.sp, color = WalletYellow.copy(alpha = 0.8f))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Surface),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "${SessionManager.getCompletedTasksCount()}", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Primary)
                                        Text(text = "Tasks Done", fontSize = 12.sp, color = Color.Gray)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = if (isActivated) "✅" else "🔒", fontSize = 20.sp)
                                        Text(text = if (isActivated) "Activated" else "Locked", fontSize = 12.sp, color = Color.Gray)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "${SessionManager.getReferralCount()}", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = WalletPurple)
                                        Text(text = "Referrals", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Your Wallet",
                                fontSize = 20.sp,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            GradientWalletCard(
                                title = "Wallet Balance",
                                amount = currencyFormat.format(user?.walletBalance ?: 0.00),
                                color = WalletOrange,
                                icon = "💰"
                            )

                            if (isActivated) {
                                GradientWalletCard(
                                    title = "Total Withdrawn",
                                    amount = currencyFormat.format(user?.totalWithdrawn ?: 0.00),
                                    color = WalletGreen,
                                    icon = "📤"
                                )
                                GradientWalletCard(
                                    title = "Total Earnings",
                                    amount = currencyFormat.format(user?.totalEarnings ?: 0.00),
                                    color = WalletBlue,
                                    icon = "📈"
                                )
                            }

                            GradientWalletCard(
                                title = "Referral Earnings",
                                amount = currencyFormat.format(SessionManager.getReferralEarnings()),
                                color = WalletPurple,
                                icon = "🎁"
                            )

                            if (!isActivated) {
                                GradientWalletCard(
                                    title = "Activation Fee",
                                    amount = "KES 100",
                                    color = WalletYellow,
                                    icon = "🔑"
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Quick Actions",
                                fontSize = 20.sp,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            AnimatedButton(
                                onClick = onTasksClick,
                                text = "📋 View Available Tasks",
                                backgroundColor = Primary,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            AnimatedButton(
                                onClick = onReferralClick,
                                text = "🎁 Refer & Earn KES 20",
                                backgroundColor = WalletPurple,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if ((user?.walletBalance ?: 0.0) > 0) {
                                AnimatedButton(
                                    onClick = onWithdrawClick,
                                    text = "💸 Withdraw Earnings",
                                    backgroundColor = WalletGreen,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // 👇 ANNOUNCEMENTS - ORANGE BACKGROUND
                            if (announcements.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "📢 Latest Announcements",
                                    fontSize = 18.sp,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFF333333)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                announcements.forEach { announcement ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (announcement.isPinned)
                                                Color(0xFFFF6F00)  // Dark Orange for pinned
                                            else
                                                Color(0xFFFFB74D)  // Light Orange for normal
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = if (announcement.isPinned) 6.dp else 3.dp
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = announcement.title,
                                                        fontSize = 16.sp,
                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                        color = if (announcement.isPinned)
                                                            Color.White
                                                        else
                                                            Color(0xFF4A2800)
                                                    )
                                                    if (announcement.isPinned) {
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Surface(
                                                            color = Color.White,
                                                            shape = RoundedCornerShape(4.dp)
                                                        ) {
                                                            Text(
                                                                text = " 📌 PINNED ",
                                                                fontSize = 10.sp,
                                                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                color = Color(0xFFFF6F00),
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = announcement.message,
                                                    fontSize = 14.sp,
                                                    color = if (announcement.isPinned)
                                                        Color.White
                                                    else
                                                        Color(0xFF4A2800),
                                                    lineHeight = 20.sp
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Text(
                                                        text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                                            .format(Date(announcement.createdAt)),
                                                        fontSize = 11.sp,
                                                        color = if (announcement.isPinned)
                                                            Color.White.copy(alpha = 0.8f)
                                                        else
                                                            Color(0xFF6D4C00)
                                                    )
                                                    if (announcement.isPinned) {
                                                        Text(
                                                            text = "📍 Pinned",
                                                            fontSize = 11.sp,
                                                            color = Color.White
                                                        )
                                                    }
                                                }
                                            }
                                            if (announcement.isPinned) {
                                                Icon(
                                                    Icons.Default.PushPin,
                                                    contentDescription = "Pinned",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}