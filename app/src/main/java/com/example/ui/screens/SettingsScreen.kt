package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.TunisiaRedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: ThemeMode,
    currentLanguage: String,
    onThemeChange: (ThemeMode) -> Unit,
    onLanguageChange: (String) -> Unit,
    onLogout: () -> Unit,
    firebaseStatus: String = "Firebase SDK Active 🔥",
    modifier: Modifier = Modifier
) {
    var hideLastSeen by remember { mutableStateOf(true) }
    var hideOnlineStatus by remember { mutableStateOf(false) }
    var hidePhoneNumber by remember { mutableStateOf(true) }
    var appLockEnabled by remember { mutableStateOf(true) }
    var isVerifiedSubscribed by remember { mutableStateOf(false) }
    var isThemeExpanded by remember { mutableStateOf(false) }
    var isLangExpanded by remember { mutableStateOf(false) }

    val languages = listOf("Tunisian Arabic (Derja)", "Arabic", "French", "English")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Settings & Preferences ⚙️",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Themes Section
        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Appearance & Themes 🎨", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = isThemeExpanded,
                    onExpandedChange = { isThemeExpanded = !isThemeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = currentTheme.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Theme Mode") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isThemeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = isThemeExpanded,
                        onDismissRequest = { isThemeExpanded = false }
                    ) {
                        ThemeMode.values().forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.name) },
                                onClick = {
                                    onThemeChange(mode)
                                    isThemeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Language Selector
                ExposedDropdownMenuBox(
                    expanded = isLangExpanded,
                    onExpandedChange = { isLangExpanded = !isLangExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = currentLanguage,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("App Language") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isLangExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = isLangExpanded,
                        onDismissRequest = { isLangExpanded = false }
                    ) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = {
                                    onLanguageChange(lang)
                                    isLangExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security & Privacy Section
        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Privacy & Security 🔐", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                SettingToggleRow("App Lock (PIN / Fingerprint)", appLockEnabled) { appLockEnabled = it }
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                SettingToggleRow("Hide Last Seen", hideLastSeen) { hideLastSeen = it }
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                SettingToggleRow("Hide Online Status", hideOnlineStatus) { hideOnlineStatus = it }
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                SettingToggleRow("Hide Phone Number", hidePhoneNumber) { hidePhoneNumber = it }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Firebase Cloud Server Status
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
            modifier = Modifier.fillMaxWidth().testTag("firebase_status_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color(0xFFEA580C))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Firebase Cloud Backend 🔥", fontWeight = FontWeight.Bold, color = Color(0xFF9A3412), style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(firebaseStatus, style = MaterialTheme.typography.bodySmall, color = Color(0xFFC2410C), fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("To connect your live Firebase project, place your `google-services.json` file inside the `/app` folder. Firestore and Auth are pre-configured.", style = MaterialTheme.typography.labelSmall, color = Color(0xFF7C2D12))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Monetization & Subscription
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TunisiaRedPrimary.copy(alpha = 0.08f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = TunisiaRedPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verified Badge Subscription", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Get blue verified badge next to your name, priority support, and custom sticker store access for 5 TND/month.", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { isVerifiedSubscribed = !isVerifiedSubscribed },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVerifiedSubscribed) Color(0xFF10B981) else TunisiaRedPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isVerifiedSubscribed) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Subscribed & Verified Active ✅")
                    } else {
                        Text("Subscribe for 5 TND / month 🚀")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = onLogout,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("logout_button")
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = TunisiaRedPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", color = TunisiaRedPrimary)
        }
    }
}

@Composable
fun SettingToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = TunisiaRedPrimary)
        )
    }
}
