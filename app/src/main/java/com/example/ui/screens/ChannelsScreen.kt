package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.TunisiaRedPrimary
import com.example.ui.theme.ZahrouniRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelsScreen(
    onChannelClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showCreateModal by remember { mutableStateOf(false) }

    var channelTitleInput by remember { mutableStateOf("") }
    var channelDescInput by remember { mutableStateOf("") }

    val channelsList = remember {
        mutableStateListOf(
            ChannelItem("Tunisie Actu News 📣", "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=150", "48.2K subscribers", "Latest breaking national news & weather in Tunisia", true, true),
            ChannelItem("Tunisian Football Express ⚽", "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=150", "32.1K subscribers", "Ligue 1, Aigles de Carthage news and match highlights", true, false),
            ChannelItem("Djerba Travel & Culture 🌴", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=150", "19.8K subscribers", "Discover beautiful secret spots across Djerba & Southern Tunisia", false, false),
            ChannelItem("Tunisie Tech & Startups 💻", "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=150", "14.5K subscribers", "Community for software developers and tech founders in Tunis", true, true)
        )
    }

    val filteredChannels = remember(channelsList, searchQuery) {
        if (searchQuery.isBlank()) channelsList
        else channelsList.filter { it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Channels & Communities 📣",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            IconButton(
                onClick = { showCreateModal = true },
                modifier = Modifier.testTag("create_channel_button")
            ) {
                Icon(Icons.Default.Campaign, contentDescription = "Create Channel", tint = TunisiaRedPrimary)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search public channels or communities...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filteredChannels) { channel ->
                var isJoined by remember { mutableStateOf(channel.isJoined) }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = channel.avatarUrl,
                            contentDescription = channel.title,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(channel.title, fontWeight = FontWeight.Bold)
                                if (channel.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.Verified, contentDescription = "Verified", tint = TunisiaRedPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(channel.subscribers, style = MaterialTheme.typography.labelSmall, color = TunisiaRedPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(channel.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { isJoined = !isJoined },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isJoined) MaterialTheme.colorScheme.surfaceVariant else TunisiaRedPrimary,
                                contentColor = if (isJoined) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            if (isJoined) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Joined")
                            } else {
                                Text("Join")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateModal) {
        AlertDialog(
            onDismissRequest = { showCreateModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = ZahrouniRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Public Channel 📣", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = channelTitleInput,
                        onValueChange = { channelTitleInput = it },
                        label = { Text("Channel Name") },
                        placeholder = { Text("e.g. Sousse Tech Hub 🇹🇳") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = channelDescInput,
                        onValueChange = { channelDescInput = it },
                        label = { Text("Description") },
                        placeholder = { Text("What is this community about?") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val title = if (channelTitleInput.isNotBlank()) channelTitleInput else "New Community Channel 🇹🇳"
                        val desc = if (channelDescInput.isNotBlank()) channelDescInput else "Official public group channel"
                        channelsList.add(
                            0,
                            ChannelItem(
                                title = title,
                                avatarUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=150",
                                subscribers = "1 subscriber (You)",
                                description = desc,
                                isVerified = true,
                                isJoined = true
                            )
                        )
                        showCreateModal = false
                        channelTitleInput = ""
                        channelDescInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZahrouniRed)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create Channel 🚀", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

data class ChannelItem(
    val title: String,
    val avatarUrl: String,
    val subscribers: String,
    val description: String,
    val isVerified: Boolean,
    val isJoined: Boolean
)
