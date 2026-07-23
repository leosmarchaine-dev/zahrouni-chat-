package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.remote.FirebaseService
import com.example.data.remote.GeminiAiService
import com.example.data.repository.ChatRepository
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class NavigationTab {
    CHATS, STORIES, CALLS, CHANNELS, AI_ASSISTANT, MARKETPLACE, ADMIN_PANEL, SETTINGS, PROFILE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val aiService = GeminiAiService()
    val firebaseService = FirebaseService(application)
    val repository = ChatRepository(database, aiService)

    val firebaseStatus: String
        get() = firebaseService.getFirebaseStatus()

    // App Preferences & States
    private val _currentTab = MutableStateFlow(NavigationTab.CHATS)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.LIGHT)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _currentLanguage = MutableStateFlow("Tunisian Arabic (Derja)")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // Auth State
    private val _currentUser = MutableStateFlow(
        User(
            id = "user_me",
            name = "Iheb Ezzine",
            username = "iheb_ezzine",
            phoneNumber = "+216 22 123 456",
            email = "iheb.ezzine@gmail.com",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
            coverUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800",
            bio = "Loving Sidi Bou Said & Tech in Tunis 🇹🇳",
            gender = "Male",
            birthday = "1998-03-20",
            country = "Tunisia",
            city = "Tunis",
            isOnline = true,
            isVerified = true,
            isPremium = true,
            role = UserRole.VERIFIED_USER
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Active Chat
    private val _activeChatId = MutableStateFlow<String?>(null)
    val activeChatId: StateFlow<String?> = _activeChatId.asStateFlow()

    val chats: StateFlow<List<Chat>> = repository.allChats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val activeMessages: StateFlow<List<Message>> = _activeChatId.flatMapLatest { chatId ->
        if (chatId != null) repository.getMessages(chatId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val aiMessages: StateFlow<List<Message>> = repository.getMessages("ai_assistant")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Call State & Logs
    private val _activeCall = MutableStateFlow<CallSession?>(null)
    val activeCall: StateFlow<CallSession?> = _activeCall.asStateFlow()

    private val _callLogs = MutableStateFlow<List<CallLogItem>>(
        listOf(
            CallLogItem("c1", "Youssef Chahed", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", "Incoming", "Today, 10:15 AM", "03:42", true, true),
            CallLogItem("c2", "Cyrine Medenine", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150", "Outgoing", "Yesterday, 6:30 PM", "01:15", false, true),
            CallLogItem("c3", "Tunisie Tech Group Call", "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=150", "Incoming", "Jul 21, 8:00 PM", "12:05", true, true),
            CallLogItem("c4", "Amine Sousse", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150", "Missed", "Jul 20, 2:10 PM", "00:00", false, true)
        )
    )
    val callLogs: StateFlow<List<CallLogItem>> = _callLogs.asStateFlow()

    // Stories Data
    private val _stories = MutableStateFlow(
        listOf(
            Story(
                id = "s1",
                userId = "cyrine",
                userName = "Cyrine Medenine",
                userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
                textContent = "Djerba sunsets hit different! 🌴🌅 #Tunisia",
                timestamp = "2h ago",
                isSeen = false
            ),
            Story(
                id = "s2",
                userId = "youssef",
                userName = "Youssef Chahed",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                mediaUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600",
                textContent = "Coffee & Bambalouni time at Sidi Bou Said ☕",
                timestamp = "4h ago",
                isSeen = true
            ),
            Story(
                id = "s3",
                userId = "sidi_bou_club",
                userName = "Tunis Arts",
                userAvatar = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=150",
                mediaUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600",
                textContent = "Carthage Music Festival live tonight! 🎵",
                timestamp = "6h ago",
                isSeen = false
            )
        )
    )
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    // AI Helper state
    private val _aiResponseLoading = MutableStateFlow(false)
    val aiResponseLoading: StateFlow<Boolean> = _aiResponseLoading.asStateFlow()

    private val _smartReplies = MutableStateFlow<List<String>>(emptyList())
    val smartReplies: StateFlow<List<String>> = _smartReplies.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialData()
        }
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun openChat(chatId: String) {
        _activeChatId.value = chatId
        // Generate quick smart replies when opening chat
        viewModelScope.launch {
            _smartReplies.value = aiService.generateSmartReplies("Aslema, labes?")
        }
    }

    fun closeChat() {
        _activeChatId.value = null
    }

    fun sendMessage(chatId: String, text: String, type: MessageType = MessageType.TEXT) {
        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                senderId = "user_me",
                senderName = _currentUser.value.name,
                content = text,
                type = type
            )

            // If messaging Gemini AI, get full unlimited response
            if (chatId == "ai_assistant") {
                _aiResponseLoading.value = true
                val aiReply = aiService.askGemini(text)
                repository.sendMessage(
                    chatId = "ai_assistant",
                    senderId = "ai",
                    senderName = "Gemini AI Assistant 🤖",
                    content = aiReply
                )
                _aiResponseLoading.value = false
            }
        }
    }

    fun addStory(textContent: String, mediaUrl: String) {
        val user = _currentUser.value
        val newStory = Story(
            id = "s_${System.currentTimeMillis()}",
            userId = user.id,
            userName = user.name,
            userAvatar = if (user.avatarUrl.isNotBlank()) user.avatarUrl else "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
            mediaUrl = if (mediaUrl.isNotBlank()) mediaUrl else "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
            textContent = if (textContent.isNotBlank()) textContent else "New Story on Zahrouni Chat! 🇹🇳",
            timestamp = "Just now",
            isSeen = false,
            viewersCount = 1
        )
        _stories.value = listOf(newStory) + _stories.value
        viewModelScope.launch {
            firebaseService.publishStoryToFirestore(newStory)
        }
    }

    fun sendAiMultimodalMessage(text: String, imageBase64: String? = null, mediaUrl: String? = null, fileText: String? = null) {
        viewModelScope.launch {
            var userMsgContent = text
            if (fileText != null && fileText.isNotBlank()) {
                userMsgContent = if (text.isNotBlank()) "$text\n\n📄 [Attached Document]:\n$fileText" else "📄 [Attached Document]:\n$fileText"
            }
            if (userMsgContent.isBlank()) {
                userMsgContent = if (imageBase64 != null) "🖼️ [Photo attached for AI analysis]" else "Hello Gemini AI!"
            }

            val msgType = if (imageBase64 != null || (mediaUrl != null && mediaUrl.isNotBlank())) MessageType.IMAGE else MessageType.TEXT

            repository.sendMessage(
                chatId = "ai_assistant",
                senderId = "user_me",
                senderName = _currentUser.value.name,
                content = userMsgContent,
                type = msgType,
                mediaUrl = mediaUrl ?: ""
            )

            _aiResponseLoading.value = true
            val fullPrompt = if (fileText != null && fileText.isNotBlank()) {
                "$userMsgContent\n\nPlease analyze the question and document above thoroughly."
            } else {
                userMsgContent
            }

            val aiReply = aiService.askGemini(prompt = fullPrompt, imageBase64 = imageBase64)
            repository.sendMessage(
                chatId = "ai_assistant",
                senderId = "ai",
                senderName = "Gemini AI Assistant 🤖",
                content = aiReply
            )
            _aiResponseLoading.value = false
        }
    }

    fun translateMessage(messageId: String, content: String, targetLang: String) {
        viewModelScope.launch {
            repository.translateMessage(messageId, content, targetLang)
        }
    }

    fun startCall(partnerName: String, partnerAvatar: String, isVideo: Boolean) {
        _activeCall.value = CallSession(
            id = "call_${System.currentTimeMillis()}",
            partnerName = partnerName,
            partnerAvatar = if (partnerAvatar.isBlank()) "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150" else partnerAvatar,
            isVideo = isVideo,
            isIncoming = false,
            duration = "00:00",
            isConnected = true,
            isVideoOn = isVideo,
            isSpeakerOn = isVideo
        )
    }

    fun updateCallDuration(durationStr: String) {
        _activeCall.value = _activeCall.value?.copy(duration = durationStr)
    }

    fun endCall() {
        val current = _activeCall.value
        if (current != null) {
            val newLog = CallLogItem(
                id = "log_${System.currentTimeMillis()}",
                name = current.partnerName,
                avatarUrl = current.partnerAvatar,
                type = if (current.isIncoming) "Incoming" else "Outgoing",
                time = "Just now",
                duration = current.duration,
                isVideo = current.isVideo,
                isHd = true
            )
            _callLogs.value = listOf(newLog) + _callLogs.value
        }
        _activeCall.value = null
    }

    fun toggleMuteCall() {
        _activeCall.value = _activeCall.value?.let { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleVideoCall() {
        _activeCall.value = _activeCall.value?.let { it.copy(isVideoOn = !it.isVideoOn) }
    }

    fun toggleScreenSharing() {
        _activeCall.value = _activeCall.value?.let { it.copy(isScreenSharing = !it.isScreenSharing) }
    }

    fun toggleSpeaker() {
        _activeCall.value = _activeCall.value?.let { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun flipCamera() {
        _activeCall.value = _activeCall.value?.let { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun updateUserProfile(name: String, username: String, bio: String, city: String, avatarUrl: String? = null) {
        val updatedUser = _currentUser.value.copy(
            name = name,
            username = username,
            bio = bio,
            city = city,
            avatarUrl = if (!avatarUrl.isNullOrBlank()) avatarUrl else _currentUser.value.avatarUrl
        )
        _currentUser.value = updatedUser
        viewModelScope.launch {
            firebaseService.saveUserToFirestore(updatedUser)
        }
    }

    fun loginWithAuth(method: AuthType, credentialInfo: String) {
        _isLoggedIn.value = true
        val nameFromEmail = if (credentialInfo.contains("@")) {
            credentialInfo.substringBefore("@").replace(".", " ").split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } }
        } else "Iheb Ezzine"

        _currentUser.value = _currentUser.value.copy(
            name = if (method == AuthType.GUEST) "Guest User" else nameFromEmail,
            username = if (method == AuthType.GUEST) "guest_tn" else if (credentialInfo.contains("@")) credentialInfo.substringBefore("@") else "iheb_ezzine",
            phoneNumber = if (!credentialInfo.contains("@") && credentialInfo != "Guest") credentialInfo else "+216 22 123 456",
            email = if (credentialInfo.contains("@")) credentialInfo else "iheb.ezzine@gmail.com"
        )
    }

    fun addNewContactByPhoneOrUsername(query: String, onAdded: (String) -> Unit = {}) {
        viewModelScope.launch {
            val newChatId = repository.addContactByPhoneOrUsername(query)
            _activeChatId.value = newChatId
            onAdded(newChatId)
        }
    }

    // ==========================================
    // MARKETPLACE STATE & FUNCTIONS
    // ==========================================
    private val _marketplaceItems = MutableStateFlow(
        listOf(
            MarketplaceItem(
                id = "item_1",
                sellerId = "user_amine",
                sellerName = "Amine Sousse 🇹🇳",
                sellerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                sellerPhone = "+216 22 987 654",
                title = "iPhone 15 Pro Max Natural Titanium 256GB",
                description = "Very clean iPhone 15 Pro Max imported from France, 99% battery health. Comes with original box and fast charger.",
                price = 3200.0,
                location = "Sousse",
                category = "Phones & Tech",
                images = listOf(
                    "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600",
                    "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600",
                    "https://images.unsplash.com/photo-1592899677977-9c10ca588bbd?w=600"
                ),
                postedTime = "10 mins ago",
                isMine = false
            ),
            MarketplaceItem(
                id = "item_2",
                sellerId = "user_me",
                sellerName = "Iheb Ezzine",
                sellerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
                sellerPhone = "+216 22 123 456",
                title = "MacBook Pro M2 Max 16-inch 32GB RAM",
                description = "Used for 4 months for Android app development in Tunis. Mint condition, zero scratches.",
                price = 4800.0,
                location = "Tunis",
                category = "Electronics",
                images = listOf(
                    "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600",
                    "https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=600"
                ),
                postedTime = "1 hour ago",
                isMine = true
            ),
            MarketplaceItem(
                id = "item_3",
                sellerId = "user_cyrine",
                sellerName = "Cyrine Medenine 🇹🇳",
                sellerAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                sellerPhone = "+216 98 123 789",
                title = "Vespa Primavera 125 Scooter Red",
                description = "Classic Vespa red scooter, perfect for riding around Sidi Bou Said & La Marsa. Fully maintained.",
                price = 4200.0,
                location = "La Marsa",
                category = "Vehicles",
                images = listOf(
                    "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=600",
                    "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?w=600"
                ),
                postedTime = "2 hours ago",
                isMine = false
            )
        )
    )
    val marketplaceItems: StateFlow<List<MarketplaceItem>> = _marketplaceItems.asStateFlow()

    private val _marketplaceNotifications = MutableStateFlow(
        listOf(
            MarketplaceNotification(
                id = "notif_1",
                friendName = "Amine Sousse",
                friendAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                productTitle = "iPhone 15 Pro Max Natural Titanium",
                productPrice = "3,200 TND",
                productId = "item_1",
                timestamp = "10 mins ago"
            )
        )
    )
    val marketplaceNotifications: StateFlow<List<MarketplaceNotification>> = _marketplaceNotifications.asStateFlow()

    private val _unreadMarketNotifications = MutableStateFlow(1)
    val unreadMarketNotifications: StateFlow<Int> = _unreadMarketNotifications.asStateFlow()

    fun clearMarketplaceNotifications() {
        _unreadMarketNotifications.value = 0
    }

    fun addNewProduct(
        title: String,
        description: String,
        price: Double,
        location: String,
        category: String,
        images: List<String>
    ) {
        val newItem = MarketplaceItem(
            id = "item_${System.currentTimeMillis()}",
            sellerId = _currentUser.value.id,
            sellerName = _currentUser.value.name,
            sellerAvatar = _currentUser.value.avatarUrl,
            sellerPhone = _currentUser.value.phoneNumber,
            title = title,
            description = description,
            price = price,
            location = location,
            category = category,
            images = if (images.isEmpty()) listOf("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600") else images.take(10),
            postedTime = "Just now",
            isMine = true
        )
        _marketplaceItems.value = listOf(newItem) + _marketplaceItems.value

        // Notify friends about the user's new product
        val friendNotif = MarketplaceNotification(
            id = "notif_${System.currentTimeMillis()}",
            friendName = _currentUser.value.name,
            friendAvatar = _currentUser.value.avatarUrl,
            productTitle = title,
            productPrice = "$price TND",
            productId = newItem.id,
            timestamp = "Just now"
        )
        _marketplaceNotifications.value = listOf(friendNotif) + _marketplaceNotifications.value
    }

    fun deleteProduct(productId: String) {
        _marketplaceItems.value = _marketplaceItems.value.filter { it.id != productId }
    }

    fun updateProductPrice(productId: String, newPrice: Double) {
        _marketplaceItems.value = _marketplaceItems.value.map { item ->
            if (item.id == productId) item.copy(price = newPrice) else item
        }
    }

    fun markProductAsSold(productId: String) {
        _marketplaceItems.value = _marketplaceItems.value.map { item ->
            if (item.id == productId) item.copy(isSold = true) else item
        }
    }

    fun simulateFriendNewProduct() {
        val friends = listOf(
            Pair("Youssef Tunis", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"),
            Pair("Cyrine Medenine", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150"),
            Pair("Amine Sousse", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150")
        )
        val selectedFriend = friends.random()
        val friendProducts = listOf(
            Triple("Sony PlayStation 5 Slim + 2 Controllers", 1850.0, "Electronics"),
            Triple("BMW 320i M-Sport 2021 Black Edition", 125000.0, "Vehicles"),
            Triple("Handmade Traditional Tunisian Carpet (Kairouan)", 750.0, "Home & Goods")
        )
        val selectedProduct = friendProducts.random()
        val newId = "item_${System.currentTimeMillis()}"

        val friendItem = MarketplaceItem(
            id = newId,
            sellerId = "user_friend_${System.currentTimeMillis()}",
            sellerName = "${selectedFriend.first} 🇹🇳",
            sellerAvatar = selectedFriend.second,
            sellerPhone = "+216 22 888 777",
            title = selectedProduct.first,
            description = "Listed by your friend ${selectedFriend.first} on Zahrouni Chat Marketplace. High quality item!",
            price = selectedProduct.second,
            location = "Tunisia",
            category = selectedProduct.third,
            images = listOf(
                "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=600",
                "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600"
            ),
            postedTime = "Just now",
            isMine = false
        )

        _marketplaceItems.value = listOf(friendItem) + _marketplaceItems.value

        val notif = MarketplaceNotification(
            id = "notif_${System.currentTimeMillis()}",
            friendName = selectedFriend.first,
            friendAvatar = selectedFriend.second,
            productTitle = selectedProduct.first,
            productPrice = "${selectedProduct.second} TND",
            productId = newId,
            timestamp = "Just now"
        )
        _marketplaceNotifications.value = listOf(notif) + _marketplaceNotifications.value
        _unreadMarketNotifications.value = _unreadMarketNotifications.value + 1
    }

    fun logout() {
        _isLoggedIn.value = false
    }
}
