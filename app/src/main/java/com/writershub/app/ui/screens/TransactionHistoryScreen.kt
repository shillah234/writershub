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
import com.writershub.app.data.repository.SessionManager
import com.writershub.app.data.model.TransactionType
import com.writershub.app.ui.components.EmptyState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionHistoryScreen(
    onBackClick: () -> Unit,
    onBrowseTasksClick: () -> Unit
) {
    val user = SessionManager.currentUser
    val transactions = user?.transactions ?: emptyList()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "KE"))
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

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
                text = "Transaction History",
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Current Balance",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = currencyFormat.format(user?.walletBalance ?: 0.0),
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Total Earned",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = currencyFormat.format(user?.totalEarnings ?: 0.0),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transactions List or Empty State
        if (transactions.isEmpty()) {
            EmptyState(
                icon = Icons.Default.History,
                title = "No Transactions Yet",
                message = "Complete tasks to see your earnings and transactions here.",
                buttonText = "Browse Tasks",
                onButtonClick = onBrowseTasksClick,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn {
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        currencyFormat = currencyFormat,
                        dateFormat = dateFormat
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: com.writershub.app.data.model.Transaction,
    currencyFormat: NumberFormat,
    dateFormat: SimpleDateFormat
) {
    val (icon, color, sign) = when (transaction.type) {
        TransactionType.TASK_EARNING -> Triple("💰", Color(0xFF4CAF50), "+")
        TransactionType.WITHDRAWAL -> Triple("💸", Color(0xFFF44336), "-")
        TransactionType.REFERRAL_BONUS -> Triple("🤝", Color(0xFF2196F3), "+")
        TransactionType.ACTIVATION_FEE -> Triple("🔑", Color(0xFFFF9800), "-")
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Text(
                text = icon,
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 12.dp)
            )

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.description,
                    fontSize = 16.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
                Text(
                    text = dateFormat.format(transaction.date),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Amount
            Text(
                text = "$sign${currencyFormat.format(transaction.amount)}",
                fontSize = 18.sp,
                color = color,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }
}