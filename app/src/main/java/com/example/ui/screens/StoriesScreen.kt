package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Story
import com.example.ui.theme.ZahrouniBlack
import com.example.ui.theme.ZahrouniBlue
import com.example.ui.theme.ZahrouniRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScreen(
    stories: List<Story>,
    onAddStory: (textContent: String, mediaUrl: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var activeStory by remember { mutableStateOf<Story?>(null) }
    var showAddStoryDialog by remember { mutableStateOf(false) }
    var storyCaptionInput by remember { mutableStateOf("") }
    var selectedMediaUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var reactionCount by remember { mutableIntStateOf(12) }

    val presetImages = listOf(
        "🌊 Sidi Bou Said" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
        "☕ Coffee & Vibes" to "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600",
        "🏛️ Carthage Ruins" to "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600",
        "🌴 Djerba Island" to "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=600",
        "💻 Tech & Coding" to "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600"
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            selectedMediaUrl = uri.toString()
        }
    }

    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Stories 🇹🇳",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Share photos & moments with friends",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddStoryDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ZahrouniRed),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("add_story_header_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Story", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // First card: Add My Story quick button
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clickable { showAddStoryDialog = true }
                            .testTag("create_story_card")
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(ZahrouniRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Post Your Story", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("Photo or status update", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // Existing stories grid
                items(stories) { story ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clickable { activeStory = story }
                            .testTag("story_item_${story.id}")
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = story.mediaUrl,
                                contentDescription = story.userName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Gradient Overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.5f),
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.8f)
                                            )
                                        )
                                    )
                            )

                            // User Info at top
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = story.userAvatar,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, ZahrouniRed, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = story.userName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Caption & Viewers at bottom
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = story.textContent,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${story.viewersCount} views",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.8f))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Story Viewer Dialog
        if (activeStory != null) {
            val story = activeStory!!
            AlertDialog(
                onDismissRequest = { activeStory = null },
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
                            IconButton(onClick = { activeStory = null }) {
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
                                onClick = { reactionCount++ },
                                colors = ButtonDefaults.buttonColors(containerColor = ZahrouniRed),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("React ❤️ ($reactionCount)")
                            }
                            OutlinedButton(
                                onClick = { activeStory = null },
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
        if (showAddStoryDialog) {
            AlertDialog(
                onDismissRequest = { showAddStoryDialog = false },
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("story_caption_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Story Photo:", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.align(Alignment.Start))
                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = ZahrouniBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pick_gallery_photo_button"),
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

                        // Preview Card
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
                            if (storyCaptionInput.isNotBlank() || selectedMediaUrl.isNotBlank() || selectedUri != null) {
                                val finalUrl = if (selectedUri != null) selectedUri.toString() else selectedMediaUrl
                                val finalCaption = if (storyCaptionInput.isNotBlank()) storyCaptionInput else "Vibes in Tunis 🇹🇳"
                                onAddStory(finalCaption, finalUrl)
                                showAddStoryDialog = false
                                storyCaptionInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZahrouniRed),
                        modifier = Modifier.testTag("publish_story_button")
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Publish Story 🚀", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddStoryDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
