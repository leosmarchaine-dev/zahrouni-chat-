package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CallLogItem
import com.example.data.model.CallSession
import com.example.ui.theme.TunisiaRedPrimary
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsScreen(
    callLogs: List<CallLogItem>,
    activeCall: CallSession?,
    onStartCall: (String, String, Boolean) -> Unit,
    onEndCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleVideo: () -> Unit,
    onToggleScreenShare: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onFlipCamera: () -> Unit,
    onUpdateDuration: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialpadModal by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredLogs = remember(callLogs, selectedFilter) {
        when (selectedFilter) {
            "Incoming" -> callLogs.filter { it.type == "Incoming" }
            "Outgoing" -> callLogs.filter { it.type == "Outgoing" }
            "Missed" -> callLogs.filter { it.type == "Missed" }
            "Video" -> callLogs.filter { it.isVideo }
            else -> callLogs
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Calls & Video 📞",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Encrypted HD Voice & 1080p Video",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Surface(
                    color = TunisiaRedPrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = TunisiaRedPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Noise Cancel", style = MaterialTheme.typography.labelSmall, color = TunisiaRedPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Call Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onStartCall("Youssef Chahed", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", false) },
                    colors = ButtonDefaults.buttonColors(containerColor = TunisiaRedPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("start_voice_call_btn")
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Voice Call", fontSize = 13.sp)
                }

                Button(
                    onClick = { onStartCall("Cyrine Medenine", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150", true) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("start_video_call_btn")
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Video Call", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = { showDialpadModal = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("open_dialpad_btn")
                ) {
                    Icon(Icons.Default.Dialpad, contentDescription = "Dialpad", tint = TunisiaRedPrimary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val filters = listOf("All", "Incoming", "Outgoing", "Missed", "Video")
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        leadingIcon = if (selectedFilter == filter) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Recent Calls Log (${filteredLogs.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.PhoneMissed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No call records found",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredLogs, key = { it.id }) { log ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box {
                                    AsyncImage(
                                        model = log.avatarUrl,
                                        contentDescription = log.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                    )
                                    if (log.isVideo) {
                                        Surface(
                                            color = Color(0xFF10B981),
                                            shape = CircleShape,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .align(Alignment.BottomEnd)
                                        ) {
                                            Icon(
                                                Icons.Default.Videocam,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .padding(2.dp)
                                                    .fillMaxSize()
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = log.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        if (log.isHd) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = Color.Black.copy(alpha = 0.08f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    "HD",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val icon = when (log.type) {
                                            "Incoming" -> Icons.Default.CallReceived
                                            "Outgoing" -> Icons.Default.CallMade
                                            else -> Icons.Default.CallMissed
                                        }
                                        val color = when (log.type) {
                                            "Incoming" -> Color(0xFF10B981)
                                            "Outgoing" -> TunisiaRedPrimary
                                            else -> Color(0xFFEF4444)
                                        }
                                        Icon(
                                            icon,
                                            contentDescription = log.type,
                                            tint = color,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${log.type} • ${log.time} • ${log.duration}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row {
                                    IconButton(onClick = { onStartCall(log.name, log.avatarUrl, false) }) {
                                        Icon(
                                            Icons.Default.Call,
                                            contentDescription = "Voice Call",
                                            tint = TunisiaRedPrimary
                                        )
                                    }
                                    IconButton(onClick = { onStartCall(log.name, log.avatarUrl, true) }) {
                                        Icon(
                                            Icons.Default.Videocam,
                                            contentDescription = "Video Call",
                                            tint = Color(0xFF10B981)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Phone Dialpad Dialog Modal
        if (showDialpadModal) {
            DialpadDialog(
                onDismiss = { showDialpadModal = false },
                onStartCall = { phoneNumber, isVideo ->
                    showDialpadModal = false
                    onStartCall(phoneNumber, "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150", isVideo)
                }
            )
        }
    }
}

@Composable
fun ActiveCallOverlay(
    call: CallSession,
    onEndCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleVideo: () -> Unit,
    onToggleScreenShare: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onFlipCamera: () -> Unit,
    onUpdateDuration: (String) -> Unit
) {
    var secondsElapsed by remember { mutableStateOf(0) }
    var flyingReactions by remember { mutableStateOf<List<Pair<String, Float>>>(emptyList()) }

    // Live Call Timer Ticker
    LaunchedEffect(call.id) {
        while (true) {
            delay(1000L)
            secondsElapsed++
            val mins = secondsElapsed / 60
            val secs = secondsElapsed % 60
            val durStr = String.format("%02d:%02d", mins, secs)
            onUpdateDuration(durStr)
        }
    }

    // Sound Wave pulse infinite transition for voice call
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF020617))
                )
            )
    ) {
        // Video Stream Background / Frame
        if (call.isVideo && call.isVideoOn) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = call.partnerAvatar,
                    contentDescription = "Video Stream",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Simulated live video motion filter overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                )

                // Front/Back camera indicator badge
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (call.isFrontCamera) Icons.Default.CameraFront else Icons.Default.CameraRear,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (call.isFrontCamera) "1080p Front HD" else "4K Rear Cam",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // PiP (Picture in Picture) Local Self Camera Preview
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 16.dp, end = 16.dp)
                        .size(width = 100.dp, height = 140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                        .background(Color.DarkGray)
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                        contentDescription = "Self Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        "You",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(4.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Screen Share Mode
        if (call.isScreenSharing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp, bottom = 180.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E293B))
                    .border(2.dp, TunisiaRedPrimary, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ScreenShare,
                        contentDescription = null,
                        tint = TunisiaRedPrimary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Sharing Phone Screen",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Zahrouni Chat Mobile View",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Floating reaction emojis
        flyingReactions.forEach { reaction ->
            Text(
                text = reaction.first,
                fontSize = 32.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 200.dp)
                    .offset(x = reaction.second.dp)
            )
        }

        // Foreground UI Elements Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (call.isVideo) Color(0xFF10B981) else TunisiaRedPrimary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (call.isVideo) "📹 HD Video Call" else "🎙️ HD Voice Call",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🔐 E2E Encrypted",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = call.partnerName,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (call.duration == "00:00") "Connecting..." else call.duration,
                    color = Color(0xFF10B981),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Central Audio Visualizer Avatar (For Voice Call or Video OFF)
            if (!call.isVideo || !call.isVideoOn) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 20.dp)
                ) {
                    // Pulsing Outer Aura Ring
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        TunisiaRedPrimary.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    AsyncImage(
                        model = call.partnerAvatar,
                        contentDescription = call.partnerName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .border(3.dp, TunisiaRedPrimary, CircleShape)
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Quick Reaction Emojis Row
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val emojis = listOf("❤️", "👍", "😂", "🔥", "🇹🇳", "👏")
                emojis.forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 22.sp,
                        modifier = Modifier.clickable {
                            val randomOffset = (-100..100).random().toFloat()
                            flyingReactions = flyingReactions + Pair(emoji, randomOffset)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main In-Call Action Control Buttons Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mute Mic Toggle
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (call.isMuted) Color.White else Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        if (call.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute",
                        tint = if (call.isMuted) Color.Black else Color.White
                    )
                }

                // Video Camera Toggle
                IconButton(
                    onClick = onToggleVideo,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (call.isVideoOn) Color.White.copy(alpha = 0.2f) else Color.White)
                ) {
                    Icon(
                        if (call.isVideoOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Video",
                        tint = if (call.isVideoOn) Color.White else Color.Black
                    )
                }

                // Camera Flip (Front / Back)
                IconButton(
                    onClick = onFlipCamera,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Default.Cameraswitch,
                        contentDescription = "Flip Camera",
                        tint = Color.White
                    )
                }

                // Speakerphone Toggle
                IconButton(
                    onClick = onToggleSpeaker,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (call.isSpeakerOn) Color(0xFF3B82F6) else Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        if (call.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Speaker",
                        tint = Color.White
                    )
                }

                // Screen Share Toggle
                IconButton(
                    onClick = onToggleScreenShare,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (call.isScreenSharing) TunisiaRedPrimary else Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Default.ScreenShare,
                        contentDescription = "Screen Share",
                        tint = Color.White
                    )
                }

                // Red End Call Button
                IconButton(
                    onClick = onEndCall,
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                        .testTag("end_call_button")
                ) {
                    Icon(
                        Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DialpadDialog(
    onDismiss: () -> Unit,
    onStartCall: (String, Boolean) -> Unit
) {
    var dialNumber by remember { mutableStateOf("+216 ") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Dialpad, contentDescription = null, tint = TunisiaRedPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tunisia Phone Dialer 🇹🇳")
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = dialNumber,
                    onValueChange = { dialNumber = it },
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = TunisiaRedPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Keypad grid 3x4
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("*", "0", "#")
                )

                keys.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        row.forEach { digit ->
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clickable { dialNumber += digit }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = digit,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (dialNumber.isNotBlank()) {
                            onStartCall(dialNumber, false)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TunisiaRedPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Voice")
                }

                Button(
                    onClick = {
                        if (dialNumber.isNotBlank()) {
                            onStartCall(dialNumber, true)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Video")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
