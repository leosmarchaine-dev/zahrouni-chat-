package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.TunisiaCities
import com.example.data.model.User
import com.example.ui.theme.TunisiaRedPrimary
import com.example.ui.theme.ZahrouniBlue
import com.example.ui.theme.ZahrouniRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User,
    onUpdateProfile: (name: String, username: String, bio: String, city: String, avatarUrl: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(user.name) }
    var username by remember { mutableStateOf(user.username) }
    var bio by remember { mutableStateOf(user.bio) }
    var selectedCity by remember { mutableStateOf(user.city) }
    var currentAvatarUrl by remember(user.avatarUrl) { mutableStateOf(user.avatarUrl) }
    
    var showQrDialog by remember { mutableStateOf(false) }
    var showAvatarDialog by remember { mutableStateOf(false) }
    var isCityExpanded by remember { mutableStateOf(false) }

    var customAvatarInput by remember { mutableStateOf(currentAvatarUrl) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val presetAvatars = listOf(
        Triple("🤵 رجل أنيق", "Elegant Man", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300"),
        Triple("💼 بدلة رسمية", "Dapper Suit", "https://images.unsplash.com/photo-1560250097-0b93528c311a?w=300"),
        Triple("🧔 كاجوال عصري", "Casual Modern", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300"),
        Triple("🎨 فنان ومبتكر", "Creative Artist", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=300"),
        Triple("👩 امرأة أنيقة", "Elegant Woman", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300"),
        Triple("👩‍💼 سيدة أعمال", "Business Woman", "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=300")
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            currentAvatarUrl = uri.toString()
            customAvatarInput = uri.toString()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Cover Photo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.tunisia_hero_banner_1784764719872),
                contentDescription = "Tunisia Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // QR Code Icon Button
            IconButton(
                onClick = { showQrDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(12.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.QrCode, contentDescription = "Profile QR", tint = Color.White)
            }
        }

        // Avatar Floating Overlap
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .offset(y = (-40).dp)
                    .size(96.dp)
                    .clip(CircleShape)
                    .border(4.dp, MaterialTheme.colorScheme.background, CircleShape)
                    .clickable { showAvatarDialog = true }
                    .testTag("change_avatar_button")
            ) {
                AsyncImage(
                    model = if (selectedUri != null) selectedUri else currentAvatarUrl,
                    contentDescription = user.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(ZahrouniRed)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PhotoCamera,
                        contentDescription = "Change Profile Photo",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp)
                .padding(horizontal = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                if (user.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.Verified, contentDescription = "Verified", tint = TunisiaRedPrimary)
                }
            }

            Text(
                text = "@$username",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Change Profile Photo Trigger Button
            OutlinedButton(
                onClick = { showAvatarDialog = true },
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ZahrouniRed),
                modifier = Modifier.testTag("edit_avatar_button")
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = ZahrouniRed, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Change Profile Photo / افتار 📸", color = ZahrouniRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_name_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                prefix = { Text("@") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tunisian City Selector Dropdown
            ExposedDropdownMenuBox(
                expanded = isCityExpanded,
                onExpandedChange = { isCityExpanded = !isCityExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCity,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("City in Tunisia 🇹🇳") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCityExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = isCityExpanded,
                    onDismissRequest = { isCityExpanded = false }
                ) {
                    TunisiaCities.list.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                selectedCity = city
                                isCityExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onUpdateProfile(name, username, bio, selectedCity, currentAvatarUrl) },
                colors = ButtonDefaults.buttonColors(containerColor = TunisiaRedPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_profile_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Profile Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Avatar Selection Modal
    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = ZahrouniRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تغيير صورة البروفايل 📸", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "اختر افتار رجل أنيق أو ارفع صورتك الخاصة",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Live Avatar Preview
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(3.dp, ZahrouniRed, CircleShape)
                    ) {
                        AsyncImage(
                            model = if (selectedUri != null) selectedUri else currentAvatarUrl,
                            contentDescription = "Avatar Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gallery Pick Button
                    Button(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = ZahrouniBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("رفع صورة من جهازك 🖼️", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("أو اختر من الأفتارات الأنيقة الجاهزة:", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(presetAvatars) { (arabicTitle, engTitle, url) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        currentAvatarUrl = url
                                        selectedUri = null
                                        customAvatarInput = url
                                    }
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = if (currentAvatarUrl == url && selectedUri == null) 3.dp else 1.dp,
                                            color = if (currentAvatarUrl == url && selectedUri == null) ZahrouniRed else Color.LightGray,
                                            shape = CircleShape
                                        )
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = engTitle,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = arabicTitle,
                                    fontSize = 11.sp,
                                    fontWeight = if (currentAvatarUrl == url) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customAvatarInput,
                        onValueChange = {
                            customAvatarInput = it
                            currentAvatarUrl = it
                            selectedUri = null
                        },
                        label = { Text("أو أدخل رابط صورة مباشرة (Image URL)") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAvatarDialog = false
                        onUpdateProfile(name, username, bio, selectedCity, currentAvatarUrl)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZahrouniRed)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حفظ الصورة / Save 🚀", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text("إلغاء / Cancel")
                }
            }
        )
    }

    // QR Code Dialog
    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = { Text("Your Zahrouni Chat QR Code 📱") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(TunisiaRedPrimary)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            tint = Color.White,
                            modifier = Modifier.size(160.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("@$username", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Scan to add me instantly on Zahrouni Chat", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showQrDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TunisiaRedPrimary)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

