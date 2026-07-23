package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Chat
import com.example.data.model.Message
import com.example.data.model.MessageType
import com.example.ui.theme.TunisiaRedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chat: Chat,
    messages: List<Message>,
    smartReplies: List<String>,
    onBackClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onTranslateMessage: (String, String, String) -> Unit,
    onStartCall: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var showAttachmentDrawer by remember { mutableStateOf(false) }
    var selectedMessageForAi by remember { mutableStateOf<Message?>(null) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Bar
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }

                    AsyncImage(
                        model = chat.avatarUrl,
                        contentDescription = chat.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = chat.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Text(
                            text = if (chat.isOnline) "Online" else "Last seen recently",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (chat.isOnline) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    IconButton(onClick = { onStartCall(false) }) {
                        Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = TunisiaRedPrimary)
                    }

                    IconButton(onClick = { onStartCall(true) }) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = TunisiaRedPrimary)
                    }

                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Search Messages 🔍") },
                                onClick = { showMoreMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Mute Notifications 🔕") },
                                onClick = { showMoreMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Clear Chat History 🗑️") },
                                onClick = { showMoreMenu = false }
                            )
                        }
                    }
                }
            }

            // Pinned Banner if any pinned message exists
            val pinnedMessage = messages.find { it.isPinned }
            if (pinnedMessage != null) {
                Surface(
                    color = TunisiaRedPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = null,
                            tint = TunisiaRedPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pinned: ${pinnedMessage.content}",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Chat Messages List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isMe = msg.senderId == "user_me"
                    MessageBubble(
                        message = msg,
                        isMe = isMe,
                        onAiClick = { selectedMessageForAi = msg }
                    )
                }
            }

            // Smart Reply Chips
            if (smartReplies.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(smartReplies) { reply ->
                        SuggestionChip(
                            onClick = {
                                onSendMessage(reply)
                            },
                            label = { Text("✨ $reply", style = MaterialTheme.typography.labelMedium) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = TunisiaRedPrimary.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }

            // Attachment Drawer
            AnimatedVisibility(visible = showAttachmentDrawer) {
                Surface(
                    tonalElevation = 6.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AttachmentItem(Icons.Default.Image, "Gallery", Color(0xFF3B82F6)) {
                            onSendMessage("📷 [Photo attached]")
                            showAttachmentDrawer = false
                        }
                        AttachmentItem(Icons.Default.CameraAlt, "Camera", TunisiaRedPrimary) {
                            onSendMessage("📸 [Camera capture]")
                            showAttachmentDrawer = false
                        }
                        AttachmentItem(Icons.Default.Description, "Document", Color(0xFF10B981)) {
                            onSendMessage("📄 Document.pdf (1.2 MB)")
                            showAttachmentDrawer = false
                        }
                        AttachmentItem(Icons.Default.LocationOn, "Location", Color(0xFFF59E0B)) {
                            onSendMessage("📍 Location: Sidi Bou Said, Tunis")
                            showAttachmentDrawer = false
                        }
                    }
                }
            }

            // Message Input Bar
            Surface(
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showAttachmentDrawer = !showAttachmentDrawer }) {
                        Icon(
                            Icons.Default.AttachFile,
                            contentDescription = "Attach",
                            tint = if (showAttachmentDrawer) TunisiaRedPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Type message...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("message_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = false,
                        maxLines = 4,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    if (inputText.isBlank()) {
                        IconButton(
                            onClick = {
                                isRecordingVoice = !isRecordingVoice
                                if (!isRecordingVoice) {
                                    onSendMessage("🎙️ Voice Note (0:08)")
                                }
                            }
                        ) {
                            Icon(
                                if (isRecordingVoice) Icons.Default.StopCircle else Icons.Default.Mic,
                                contentDescription = "Voice Record",
                                tint = if (isRecordingVoice) TunisiaRedPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    onSendMessage(inputText)
                                    inputText = ""
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(TunisiaRedPrimary)
                                .testTag("send_message_button")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // AI Assistant Message Menu Dialog
        if (selectedMessageForAi != null) {
            val msg = selectedMessageForAi!!
            AlertDialog(
                onDismissRequest = { selectedMessageForAi = null },
                title = { Text("AI Assistant Actions 🤖") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Message: \"${msg.content}\"", fontWeight = FontWeight.SemiBold)
                        HorizontalDivider()
                        TextButton(
                            onClick = {
                                onTranslateMessage(msg.id, msg.content, "Tunisian Arabic")
                                selectedMessageForAi = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🇹🇳 Translate to Tunisian Derja")
                        }
                        TextButton(
                            onClick = {
                                onTranslateMessage(msg.id, msg.content, "French")
                                selectedMessageForAi = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🇫🇷 Translate to French")
                        }
                        TextButton(
                            onClick = {
                                onTranslateMessage(msg.id, msg.content, "English")
                                selectedMessageForAi = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🇬🇧 Translate to English")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedMessageForAi = null }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isMe: Boolean,
    onAiClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isMe) TunisiaRedPrimary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isMe && message.senderName.isNotEmpty()) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TunisiaRedPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                )

                if (message.translatedText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.15f))
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "✨ ${message.translatedText}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isMe) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onAiClick,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "AI Action",
                            tint = if (isMe) Color.White.copy(alpha = 0.8f) else TunisiaRedPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "12:30 PM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isMe) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        if (isMe) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.DoneAll,
                                contentDescription = "Read",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttachmentItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
