package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AuthType
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
            val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
            val activeChatId by viewModel.activeChatId.collectAsStateWithLifecycle()
            val activeCall by viewModel.activeCall.collectAsStateWithLifecycle()
            val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
            val chats by viewModel.chats.collectAsStateWithLifecycle()
            val activeMessages by viewModel.activeMessages.collectAsStateWithLifecycle()
            val smartReplies by viewModel.smartReplies.collectAsStateWithLifecycle()
            val stories by viewModel.stories.collectAsStateWithLifecycle()
            val language by viewModel.currentLanguage.collectAsStateWithLifecycle()
            val aiLoading by viewModel.aiResponseLoading.collectAsStateWithLifecycle()
            val aiMessages by viewModel.aiMessages.collectAsStateWithLifecycle()
            val callLogs by viewModel.callLogs.collectAsStateWithLifecycle()
            val marketplaceItems by viewModel.marketplaceItems.collectAsStateWithLifecycle()
            val marketplaceNotifications by viewModel.marketplaceNotifications.collectAsStateWithLifecycle()
            val unreadMarketNotifications by viewModel.unreadMarketNotifications.collectAsStateWithLifecycle()

            TunisiaChatTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (!isLoggedIn) {
                            AuthScreen(
                                onLoginSuccess = { authType, credential ->
                                    viewModel.loginWithAuth(authType, credential)
                                }
                            )
                        } else if (activeChatId != null) {
                            val activeChat = chats.find { it.id == activeChatId }
                            if (activeChat != null) {
                                ChatDetailScreen(
                                    chat = activeChat,
                                    messages = activeMessages,
                                    smartReplies = smartReplies,
                                    onBackClick = { viewModel.closeChat() },
                                    onSendMessage = { text -> viewModel.sendMessage(activeChat.id, text) },
                                    onTranslateMessage = { msgId, content, targetLang ->
                                        viewModel.translateMessage(msgId, content, targetLang)
                                    },
                                    onStartCall = { isVideo ->
                                        viewModel.startCall(activeChat.name, activeChat.avatarUrl, isVideo)
                                    }
                                )
                            } else {
                                viewModel.closeChat()
                            }
                        } else {
                            MainScaffold(
                                currentTab = currentTab,
                                viewModel = viewModel,
                                currentUser = currentUser,
                                chats = chats,
                                stories = stories,
                                aiMessages = aiMessages,
                                callLogs = callLogs,
                                activeCall = activeCall,
                                language = language,
                                aiLoading = aiLoading,
                                marketplaceItems = marketplaceItems,
                                marketplaceNotifications = marketplaceNotifications,
                                unreadMarketNotifications = unreadMarketNotifications
                            )
                        }

                        // Global Active Call Screen Overlay
                        if (activeCall != null) {
                            ActiveCallOverlay(
                                call = activeCall!!,
                                onEndCall = { viewModel.endCall() },
                                onToggleMute = { viewModel.toggleMuteCall() },
                                onToggleVideo = { viewModel.toggleVideoCall() },
                                onToggleScreenShare = { viewModel.toggleScreenSharing() },
                                onToggleSpeaker = { viewModel.toggleSpeaker() },
                                onFlipCamera = { viewModel.flipCamera() },
                                onUpdateDuration = { dur -> viewModel.updateCallDuration(dur) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    currentTab: NavigationTab,
    viewModel: MainViewModel,
    currentUser: com.example.data.model.User,
    chats: List<com.example.data.model.Chat>,
    stories: List<com.example.data.model.Story>,
    aiMessages: List<com.example.data.model.Message>,
    callLogs: List<com.example.data.model.CallLogItem>,
    activeCall: com.example.data.model.CallSession?,
    language: String,
    aiLoading: Boolean,
    marketplaceItems: List<com.example.data.model.MarketplaceItem>,
    marketplaceNotifications: List<com.example.data.model.MarketplaceNotification>,
    unreadMarketNotifications: Int
) {
    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                color = TunisiaRedPrimary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(ZahrouniBlack),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Z", color = ZahrouniRed, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Zahrouni Chat",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.selectTab(NavigationTab.ADMIN_PANEL) },
                            modifier = Modifier.testTag("admin_panel_button")
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Dashboard",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { viewModel.selectTab(NavigationTab.PROFILE) },
                            modifier = Modifier.testTag("profile_button")
                        ) {
                            Box {
                                coil.compose.AsyncImage(
                                    model = currentUser.avatarUrl,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                        .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_navigation_bar")
                ) {
                    val unreadTotal = chats.sumOf { it.unreadCount }

                    NavigationBarItem(
                        selected = currentTab == NavigationTab.CHATS,
                        onClick = { viewModel.selectTab(NavigationTab.CHATS) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (unreadTotal > 0) {
                                        Badge(containerColor = TunisiaRedPrimary) {
                                            Text(unreadTotal.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    if (currentTab == NavigationTab.CHATS) Icons.Default.ChatBubble else Icons.Default.ChatBubbleOutline,
                                    contentDescription = "Chats"
                                )
                            }
                        },
                        label = { Text("Chats", fontWeight = if (currentTab == NavigationTab.CHATS) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TunisiaRedPrimary,
                            selectedTextColor = TunisiaRedPrimary,
                            indicatorColor = TunisiaRedPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )

                    NavigationBarItem(
                        selected = currentTab == NavigationTab.STORIES,
                        onClick = { viewModel.selectTab(NavigationTab.STORIES) },
                        icon = {
                            Icon(
                                if (currentTab == NavigationTab.STORIES) Icons.Default.AutoAwesomeMotion else Icons.Default.AutoAwesomeMotion,
                                contentDescription = "Stories"
                            )
                        },
                        label = { Text("Stories", fontWeight = if (currentTab == NavigationTab.STORIES) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TunisiaRedPrimary,
                            selectedTextColor = TunisiaRedPrimary,
                            indicatorColor = TunisiaRedPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )

                    NavigationBarItem(
                        selected = currentTab == NavigationTab.CALLS,
                        onClick = { viewModel.selectTab(NavigationTab.CALLS) },
                        icon = {
                            Icon(
                                if (currentTab == NavigationTab.CALLS) Icons.Default.Call else Icons.Default.Call,
                                contentDescription = "Calls"
                            )
                        },
                        label = { Text("Calls", fontWeight = if (currentTab == NavigationTab.CALLS) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TunisiaRedPrimary,
                            selectedTextColor = TunisiaRedPrimary,
                            indicatorColor = TunisiaRedPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )

                    NavigationBarItem(
                        selected = currentTab == NavigationTab.CHANNELS,
                        onClick = { viewModel.selectTab(NavigationTab.CHANNELS) },
                        icon = {
                            Icon(
                                if (currentTab == NavigationTab.CHANNELS) Icons.Default.Campaign else Icons.Default.Campaign,
                                contentDescription = "Channels"
                            )
                        },
                        label = { Text("Channels", fontWeight = if (currentTab == NavigationTab.CHANNELS) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TunisiaRedPrimary,
                            selectedTextColor = TunisiaRedPrimary,
                            indicatorColor = TunisiaRedPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )

                    NavigationBarItem(
                        selected = currentTab == NavigationTab.AI_ASSISTANT,
                        onClick = { viewModel.selectTab(NavigationTab.AI_ASSISTANT) },
                        icon = {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "AI Assistant",
                                tint = if (currentTab == NavigationTab.AI_ASSISTANT) TunisiaRedPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = { Text("AI", fontWeight = if (currentTab == NavigationTab.AI_ASSISTANT) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TunisiaRedPrimary,
                            selectedTextColor = TunisiaRedPrimary,
                            indicatorColor = TunisiaRedPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )

                    NavigationBarItem(
                        selected = currentTab == NavigationTab.MARKETPLACE,
                        onClick = { viewModel.selectTab(NavigationTab.MARKETPLACE) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (unreadMarketNotifications > 0) {
                                        Badge(containerColor = TunisiaRedPrimary) {
                                            Text(unreadMarketNotifications.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Storefront,
                                    contentDescription = "Market Place",
                                    tint = if (currentTab == NavigationTab.MARKETPLACE) TunisiaRedPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        label = { Text("Market 🛒", fontWeight = if (currentTab == NavigationTab.MARKETPLACE) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TunisiaRedPrimary,
                            selectedTextColor = TunisiaRedPrimary,
                            indicatorColor = TunisiaRedPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.minimumInteractiveComponentSize().testTag("marketplace_tab")
                    )

                    NavigationBarItem(
                        selected = currentTab == NavigationTab.SETTINGS,
                        onClick = { viewModel.selectTab(NavigationTab.SETTINGS) },
                        icon = {
                            Icon(
                                if (currentTab == NavigationTab.SETTINGS) Icons.Default.Settings else Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings", fontWeight = if (currentTab == NavigationTab.SETTINGS) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TunisiaRedPrimary,
                            selectedTextColor = TunisiaRedPrimary,
                            indicatorColor = TunisiaRedPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.CHATS -> {
                    ChatsScreen(
                        chats = chats,
                        stories = stories,
                        onChatClick = { chatId -> viewModel.openChat(chatId) },
                        onNewChatClick = { viewModel.openChat("chat_youssef") },
                        onAddContact = { query -> viewModel.addNewContactByPhoneOrUsername(query) },
                        onAddStory = { textContent, mediaUrl -> viewModel.addStory(textContent, mediaUrl) }
                    )
                }
                NavigationTab.STORIES -> {
                    StoriesScreen(
                        stories = stories,
                        onAddStory = { textContent, mediaUrl ->
                            viewModel.addStory(textContent, mediaUrl)
                        }
                    )
                }
                NavigationTab.CALLS -> {
                    CallsScreen(
                        callLogs = callLogs,
                        activeCall = activeCall,
                        onStartCall = { partnerName, avatar, isVideo ->
                            viewModel.startCall(partnerName, avatar, isVideo)
                        },
                        onEndCall = { viewModel.endCall() },
                        onToggleMute = { viewModel.toggleMuteCall() },
                        onToggleVideo = { viewModel.toggleVideoCall() },
                        onToggleScreenShare = { viewModel.toggleScreenSharing() },
                        onToggleSpeaker = { viewModel.toggleSpeaker() },
                        onFlipCamera = { viewModel.flipCamera() },
                        onUpdateDuration = { dur -> viewModel.updateCallDuration(dur) }
                    )
                }
                NavigationTab.CHANNELS -> {
                    ChannelsScreen(onChannelClick = { chatId -> viewModel.openChat(chatId) })
                }
                NavigationTab.AI_ASSISTANT -> {
                    AiAssistantScreen(
                        messages = aiMessages,
                        onSendMessage = { prompt, imageBase64, mediaUrl, fileText ->
                            viewModel.sendAiMultimodalMessage(prompt, imageBase64, mediaUrl, fileText)
                        },
                        isLoading = aiLoading
                    )
                }
                NavigationTab.MARKETPLACE -> {
                    MarketplaceScreen(
                        items = marketplaceItems,
                        notifications = marketplaceNotifications,
                        unreadNotifCount = unreadMarketNotifications,
                        onAddProduct = { title, desc, price, loc, cat, imgs ->
                            viewModel.addNewProduct(title, desc, price, loc, cat, imgs)
                        },
                        onDeleteProduct = { id -> viewModel.deleteProduct(id) },
                        onUpdatePrice = { id, newPrice -> viewModel.updateProductPrice(id, newPrice) },
                        onMarkSold = { id -> viewModel.markProductAsSold(id) },
                        onContactSeller = { name, phone ->
                            viewModel.addNewContactByPhoneOrUsername(phone)
                        },
                        onSimulateFriendUpload = { viewModel.simulateFriendNewProduct() },
                        onClearNotifs = { viewModel.clearMarketplaceNotifications() }
                    )
                }
                NavigationTab.ADMIN_PANEL -> {
                    AdminDashboardScreen()
                }
                NavigationTab.SETTINGS -> {
                    val currentThemeMode by viewModel.themeMode.collectAsStateWithLifecycle()
                    SettingsScreen(
                        currentTheme = currentThemeMode,
                        currentLanguage = language,
                        onThemeChange = { mode -> viewModel.setThemeMode(mode) },
                        onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                        onLogout = { viewModel.logout() },
                        firebaseStatus = viewModel.firebaseStatus
                    )
                }
                NavigationTab.PROFILE -> {
                    ProfileScreen(
                        user = currentUser,
                        onUpdateProfile = { name, username, bio, city, avatarUrl ->
                            viewModel.updateUserProfile(name, username, bio, city, avatarUrl)
                        }
                    )
                }
            }
        }
    }
}
