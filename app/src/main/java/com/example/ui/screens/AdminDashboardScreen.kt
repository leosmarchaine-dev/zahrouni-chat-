package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TunisiaRedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    modifier: Modifier = Modifier
) {
    var pushTitle by remember { mutableStateOf("Festive Offer 🇹🇳") }
    var pushBody by remember { mutableStateOf("Get 50% off Zahrouni Chat Verified Badge Subscription today!") }
    var pushSentMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Web Admin Dashboard 🛠️",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Zahrouni Chat Control Center & Analytics",
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Surface(
                color = TunisiaRedPrimary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Admin",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Analytics Row Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard("Daily Active Users", "1.24 M", "+14% ↗", Color(0xFF10B981), Modifier.weight(1f))
            MetricCard("Monthly Revenue", "$48.5K", "+22% ↗", TunisiaRedPrimary, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard("Total Messages Today", "8.9 M", "+18% ↗", Color(0xFF0284C7), Modifier.weight(1f))
            MetricCard("Pending Reports", "12", "-5% ↘", Color(0xFFF59E0B), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Push Notification Sender Box
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Broadcast Push Notification 📣", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = pushTitle,
                    onValueChange = { pushTitle = it },
                    label = { Text("Notification Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pushBody,
                    onValueChange = { pushBody = it },
                    label = { Text("Notification Body") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        pushSentMessage = "Push broadcast dispatched to 1.24M active users in Tunisia! 🚀"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TunisiaRedPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Send Broadcast Push")
                }

                if (pushSentMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(pushSentMessage!!, color = Color(0xFF10B981), style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // User Management Section
        Text("User Moderation & Reports 🛡️", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(10.dp))

        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                UserReportRow("user_spam_99", "Spam messages in Tech TN", "Pending")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                UserReportRow("channel_fake_actu", "Unverified news channel copy", "Investigating")
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    change: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(shape = RoundedCornerShape(16.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(4.dp))
            Text(change, style = MaterialTheme.typography.labelSmall.copy(color = accentColor, fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun UserReportRow(target: String, reason: String, status: String) {
    var isBanned by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("@$target", fontWeight = FontWeight.Bold)
            Text(reason, style = MaterialTheme.typography.bodySmall)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
                onClick = { isBanned = !isBanned },
                colors = ButtonDefaults.textButtonColors(contentColor = if (isBanned) Color.Gray else TunisiaRedPrimary)
            ) {
                Text(if (isBanned) "Unban" else "Ban User")
            }
        }
    }
}
