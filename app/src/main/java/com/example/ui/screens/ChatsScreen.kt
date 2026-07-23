package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Chat
import com.example.data.model.ChatType
import com.example.data.model.Story
import com.example.ui.theme.TunisiaRedPrimary

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ui.theme.ZahrouniBlue
import com.example.ui.theme.ZahrouniRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    chats: List<Chat>,
    stories: List<Story>,
    onChatClick: (String) -> Unit,
    onNewChatClick: () -> Unit,
    onAddContact: (String) -> Unit = {},
    onAddStory: (textContent: String, mediaUrl: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var showAddContactDialog by remember { mutableStateOf(false) }
    var newContactQuery by remember { mutableStateOf("") }
    var selectedStoryToView by remember { mutableStateOf<Story?>(null) }
    var showAddStoryModal by remember { mutableStateOf(false) }
    var storyCaptionInput by remember { mutableStateOf("") }
    var selectedMediaUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var storyReactionCount by remember { mutableIntStateOf(15) }

    val presetImages = listOf(
        "🌊 Sidi Bou Said" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
        "☕ Coffee & Vibes" to "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600",
        "🏛️ Carthage Ruins" to "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600",
        "🌴 Djerba Island" to "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=600"
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            selectedMediaUrl = uri.toString()
        }
    }

    val filters = listOf("All", "Direct", "Groups", "Channels", "Communities")

    val filteredChats = remember(chats, searchQuery, selectedFilter) {
        chats.filter { chat ->
            val matchesFilter = when (selectedFilter) {
                "Direct" -> chat.type == ChatType.DIRECT
                "Groups" -> chat.type == ChatType.GROUP
                "Channels" -> chat.type == ChatType.CHANNEL
                "Communities" -> chat.type == ChatType.COMMUNITY
                else -> true
            }
            val matchesSearch = chat.name.contains(searchQuery, ignoreCase = true) ||
                    chat.lastMessage.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 2.dp
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search messages, contacts or channels...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TunisiaRedPrimary) },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                            IconButton(
                                onClick = { showAddContactDialog = true },
                                modifier = Modifier.testTag("add_contact_button")
                            ) {
                                Icon(
                                    Icons.Default.PersonAdd,
                                    contentDescription = "Add Friend by Phone or Username",
                                    tint = TunisiaRedPrimary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chat_search_bar")
                )
            }

            // Stories Tray Horizontal Scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stories 🇹🇳",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Surface(
                    color = TunisiaRedPrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${stories.size} Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = TunisiaRedPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                item {
                    // Create Story Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { showAddStoryModal = true }
                            .testTag("chats_add_story_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        colors = listOf(TunisiaRedPrimary.copy(alpha = 0.2f), Color(0xFFF59E0B).copy(alpha = 0.2f))
                                    )
                                )
                                .border(2.dp, TunisiaRedPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(TunisiaRedPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Story", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Add Story", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }
                }

                items(stories) { story ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedStoryToView = story }
                            .testTag("chats_story_item_${story.id}")
                    ) {
                        val storyBorderGradient = if (story.isSeen) {
                            androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color.Gray, Color.LightGray))
                        } else {
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(TunisiaRedPrimary, Color(0xFFF59E0B), Color(0xFF0284C7))
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(storyBorderGradient)
                                .padding(2.5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(2.dp)
                            ) {
                                AsyncImage(
                                    model = story.userAvatar,
                                    contentDescription = story.userName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = story.userName.split(" ").first(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Category Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TunisiaRedPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Chat List
            if (filteredChats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No chats found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredChats, key = { it.id }) { chat ->
                        ChatItemRow(
                            chat = chat,
                            onClick = { onChatClick(chat.id) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }

        // Floating Action Button for Adding New Friend/Chat
        FloatingActionButton(
            onClick = { showAddContactDialog = true },
            containerColor = TunisiaRedPrimary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("new_chat_fab")
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = "Add Friend")
        }

        // Add Friend Dialog (Phone Number or Profile Username)
        if (showAddContactDialog) {
            AlertDialog(
                onDismissRequest = { showAddContactDialog = false },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = TunisiaRedPrimary, modifier = Modifier.size(32.dp)) },
                title = {
                    Text(
                        "إضافة صديق / Add Friend 🇹🇳",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "أدخل رقم هاتف الصديق أو اسم البروفايل للإضافة مباشرة:\nEnter your friend's Phone Number (+216...) or Profile Username (@name) to start chatting:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = newContactQuery,
                            onValueChange = { newContactQuery = it },
                            placeholder = { Text("+216 22 999 888 or @youssef_tn") },
                            leadingIcon = { Icon(Icons.Default.ContactPage, contentDescription = null, tint = TunisiaRedPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_contact_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick suggestion chips
                        Text("Suggested Friends in Tunisia:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SuggestionChip(
                                onClick = { newContactQuery = "+216 98 765 432" },
                                label = { Text("+216 98 765 432", fontSize = 11.sp) }
                            )
                            SuggestionChip(
                                onClick = { newContactQuery = "@amine_sousse" },
                                label = { Text("@amine_sousse", fontSize = 11.sp) }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newContactQuery.isNotBlank()) {
                                onAddContact(newContactQuery)
                                showAddContactDialog = false
                                newContactQuery = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TunisiaRedPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add & Start Chat 💬", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddContactDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Active Story Viewer Dialog
        if (selectedStoryToView != null) {
            val story = selectedStoryToView!!
            AlertDialog(
                onDismissRequest = { selectedStoryToView = null },
                confirmButton = {},
                dismissButton = {},
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = story.userAvatar,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(CircleShape).border(1.dp, ZahrouniRed, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(story.userName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(story.timestamp, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { selectedStoryToView = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(shape = RoundedCornerShape(16.dp)) {
                            Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                                AsyncImage(
                                    model = story.mediaUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(story.textContent, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { storyReactionCount++ },
                                colors = ButtonDefaults.buttonColors(containerColor = ZahrouniRed),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("React ❤️ ($storyReactionCount)")
                            }
                            OutlinedButton(
                                onClick = { selectedStoryToView = null },
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Close")
                            }
                        }
                    }
                }
            )
        }

        // Add Story Dialog
        if (showAddStoryModal) {
            AlertDialog(
                onDismissRequest = { showAddStoryModal = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = ZahrouniRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create New Story 📸", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = storyCaptionInput,
                            onValueChange = { storyCaptionInput = it },
                            label = { Text("What's on your mind? (Caption)") },
                            placeholder = { Text("e.g. Sunny day in Sidi Bou Said! 🌊") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Story Photo:", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.align(Alignment.Start))
                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = ZahrouniBlue),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pick Photo from Device Gallery")
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Or choose a featured preset:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.Start))
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(presetImages) { (label, url) ->
                                FilterChip(
                                    selected = selectedMediaUrl == url,
                                    onClick = {
                                        selectedMediaUrl = url
                                        selectedUri = null
                                    },
                                    label = { Text(label, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ZahrouniRed,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = if (selectedUri != null) selectedUri else selectedMediaUrl,
                                    contentDescription = "Story Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.4f))
                                )
                                Text(
                                    text = if (storyCaptionInput.isNotBlank()) storyCaptionInput else "Live Caption Preview...",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(12.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val finalUrl = if (selectedUri != null) selectedUri.toString() else selectedMediaUrl
                            val finalCaption = if (storyCaptionInput.isNotBlank()) storyCaptionInput else "Vibes in Tunis 🇹🇳"
                            onAddStory(finalCaption, finalUrl)
                            showAddStoryModal = false
                            storyCaptionInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZahrouniRed)
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Publish Story 🚀", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddStoryModal = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ChatItemRow(
    chat: Chat,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            AsyncImage(
                model = chat.avatarUrl,
                contentDescription = chat.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            if (chat.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = chat.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (chat.isVerifiedChannel) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = TunisiaRedPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = chat.lastMessageTime,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (chat.unreadCount > 0) TunisiaRedPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = chat.lastMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (chat.isPinned) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (chat.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(TunisiaRedPrimary)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chat.unreadCount.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
