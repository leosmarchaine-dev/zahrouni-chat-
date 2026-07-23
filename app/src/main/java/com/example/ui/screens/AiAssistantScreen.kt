package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Message
import com.example.data.model.MessageType
import com.example.ui.theme.ZahrouniBlack
import com.example.ui.theme.ZahrouniBlue
import com.example.ui.theme.ZahrouniRed
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    messages: List<Message> = emptyList(),
    onSendMessage: (prompt: String, imageBase64: String?, mediaUrl: String?, fileText: String?) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var promptInput by remember { mutableStateOf("") }
    var selectedImageBase64 by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    var attachedFileName by remember { mutableStateOf<String?>(null) }
    var attachedFileText by remember { mutableStateOf<String?>(null) }
    var showPresetMediaDialog by remember { mutableStateOf(false) }
    var mediaDialogType by remember { mutableStateOf("PHOTO") } // "PHOTO" or "FILE"

    val listState = rememberLazyListState()

    val presetPrompts = listOf(
        "💡 Explain Artificial Intelligence simply",
        "📷 Analyze attached photo and describe it",
        "🇹🇳 ترجم هاته الجملة للدارجة التونسية",
        "👨‍💻 Write a Jetpack Compose UI function",
        "📄 Summarize attached document file"
    )

    val samplePhotos = listOf(
        Triple("🌊 Sidi Bou Said Coast", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600", "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="),
        Triple("☕ Cafe & Pastry Receipt", "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600", "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="),
        Triple("💻 Jetpack Compose Code", "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600", "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==")
    )

    val sampleDocuments = listOf(
        Pair("📄 Tunisia_Tech_Report_2026.pdf", "Official Report: Tunisia Mobile & Digital Economy 2026.\nKey Highlights:\n1. 5G deployment reaches 85% coverage in Tunis, Sfax and Sousse.\n2. E-Commerce transactions increased by 42% YoY.\n3. Zahrouni Chat platform selected as top local AI & messaging application."),
        Pair("🐍 Machine_Learning_Model.py", "# Machine Learning Classifier Script\nimport numpy as np\nfrom sklearn.ensemble import RandomForestClassifier\n\nX_train = np.random.rand(100, 10)\ny_train = np.random.randint(0, 2, 100)\nmodel = RandomForestClassifier(n_estimators=50)\nmodel.fit(X_train, y_train)\nprint('Model Accuracy: 96.4%')"),
        Pair("📝 Project_Meeting_Notes.docx", "Project Meeting Minutes - Zahrouni Chat Release\nParticipants: Iheb Ezzine, Tech Team.\nAgenda:\n- Enable Gemini AI multimodal image & file support.\n- Upgrade registration with instant OTP verification code.\n- Launch marketplace and stories feature.")
    )

    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            selectedImageUrl = uri.toString()
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val baos = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 75, baos)
                selectedImageBase64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
            } catch (e: Exception) {
                selectedImageBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="
            }
        }
    }

    // Document / File Picker Launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            attachedFileName = uri.lastPathSegment ?: "Attached_Document.pdf"
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val textContent = inputStream?.bufferedReader()?.use { it.readText() }
                attachedFileText = textContent ?: "Content extracted from $attachedFileName"
            } catch (e: Exception) {
                attachedFileText = "Extracted content from document: $attachedFileName"
            }
        }
    }

    // Auto scroll when new messages arrive
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(ZahrouniRed, ZahrouniBlue)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Gemini AI Unlimited 🤖",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Multimodal",
                            color = Color(0xFF10B981),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Vision Photos 📷 • Document Analysis 📄 • Derja & English 🇹🇳",
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Preset Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            items(presetPrompts) { prompt ->
                FilterChip(
                    selected = false,
                    onClick = { promptInput = prompt },
                    label = { Text(prompt, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Message Feed
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { message ->
                val isMe = message.senderId == "user_me"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    if (!isMe) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(ZahrouniRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        ),
                        color = if (isMe) ZahrouniBlue else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.widthIn(max = 290.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (!isMe) {
                                Text(
                                    text = message.senderName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ZahrouniRed
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            if (message.type == MessageType.IMAGE && message.mediaUrl.isNotBlank()) {
                                AsyncImage(
                                    model = message.mediaUrl,
                                    contentDescription = "Attached Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface
                            )

                            if (!isMe) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = { clipboardManager.setText(AnnotatedString(message.content)) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy text", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = ZahrouniRed,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Gemini AI is analyzing & generating answer...", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Active Attachment Preview Badges
        if (selectedImageBase64 != null || selectedImageUri != null || selectedImageUrl != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedImageUrl != null || selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri ?: selectedImageUrl,
                            contentDescription = "Attached Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        Icon(Icons.Default.Image, contentDescription = null, tint = ZahrouniRed, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("📷 Photo Attached for AI Vision", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Gemini will analyze photo + text prompt", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = {
                        selectedImageBase64 = null
                        selectedImageUri = null
                        selectedImageUrl = null
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Remove Image", tint = Color.Gray)
                    }
                }
            }
        }

        if (attachedFileName != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ZahrouniBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = ZahrouniBlue)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("📄 Attached: $attachedFileName", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("File text ready for Gemini analysis", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = {
                        attachedFileName = null
                        attachedFileText = null
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Remove File", tint = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Input Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo Button
            IconButton(
                onClick = {
                    mediaDialogType = "PHOTO"
                    showPresetMediaDialog = true
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("ai_attach_image_button")
            ) {
                Icon(
                    Icons.Default.AddPhotoAlternate,
                    contentDescription = "Attach Photo for AI Vision Analysis",
                    tint = ZahrouniRed
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // File / Document Button
            IconButton(
                onClick = {
                    mediaDialogType = "FILE"
                    showPresetMediaDialog = true
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("ai_attach_file_button")
            ) {
                Icon(
                    Icons.Default.AttachFile,
                    contentDescription = "Attach Document File for AI Analysis",
                    tint = ZahrouniBlue
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Text Input Field
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                placeholder = { Text("Ask Gemini AI or attach photo/file...", fontSize = 13.sp) },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_prompt_input")
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Send Button
            IconButton(
                onClick = {
                    if (promptInput.isNotBlank() || selectedImageBase64 != null || selectedImageUrl != null || attachedFileText != null) {
                        onSendMessage(
                            promptInput,
                            selectedImageBase64,
                            selectedImageUrl ?: selectedImageUri?.toString(),
                            attachedFileText
                        )
                        promptInput = ""
                        selectedImageBase64 = null
                        selectedImageUri = null
                        selectedImageUrl = null
                        attachedFileName = null
                        attachedFileText = null
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ZahrouniRed)
                    .testTag("ai_send_button")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send to AI",
                    tint = Color.White
                )
            }
        }

        // Attachment Source Modal (Gallery / Presets / Documents)
        if (showPresetMediaDialog) {
            AlertDialog(
                onDismissRequest = { showPresetMediaDialog = false },
                title = {
                    Text(
                        text = if (mediaDialogType == "PHOTO") "Attach Photo for AI Vision 📷" else "Attach Document / File 📄",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (mediaDialogType == "PHOTO") {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch("image/*")
                                    showPresetMediaDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ZahrouniRed),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Pick from Device Gallery")
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Or select sample photo for analysis:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))

                            samplePhotos.forEach { (title, url, b64) ->
                                Surface(
                                    onClick = {
                                        selectedImageUrl = url
                                        selectedImageBase64 = b64
                                        showPresetMediaDialog = false
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = url,
                                            contentDescription = title,
                                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(6.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    filePickerLauncher.launch("*/*")
                                    showPresetMediaDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ZahrouniBlue),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Browse Device Files (.pdf, .txt, .docx, .py)")
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Or select sample document file:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))

                            sampleDocuments.forEach { (fileName, textSnippet) ->
                                Surface(
                                    onClick = {
                                        attachedFileName = fileName
                                        attachedFileText = textSnippet
                                        showPresetMediaDialog = false
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Description, contentDescription = null, tint = ZahrouniBlue)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(fileName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(textSnippet.take(45) + "...", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showPresetMediaDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
