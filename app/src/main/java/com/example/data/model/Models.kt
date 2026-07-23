package com.example.data.model

enum class AuthType {
    PHONE_OTP, EMAIL, GOOGLE, FACEBOOK, GUEST
}

enum class UserRole {
    USER, VERIFIED_USER, PREMIUM, ADMIN
}

data class User(
    val id: String,
    val name: String,
    val username: String,
    val phoneNumber: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val coverUrl: String = "",
    val bio: String = "Sidi Bou Said vibes 🇹🇳 | Zahrouni Chat",
    val gender: String = "Not specified",
    val birthday: String = "2000-01-01",
    val country: String = "Tunisia",
    val city: String = "Tunis",
    val isOnline: Boolean = true,
    val lastSeen: String = "Just now",
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val role: UserRole = UserRole.USER,
    val qrCode: String = "tnchat://user/"
)

enum class MessageType {
    TEXT, IMAGE, VIDEO, VOICE_NOTE, DOCUMENT, LOCATION, CONTACT, STICKER, GIF
}

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String = "",
    val content: String,
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isPinned: Boolean = false,
    val replyToId: String? = null,
    val replyToText: String? = null,
    val reactions: Map<String, Int> = emptyMap(), // e.g. "❤️" -> 5
    val isEdited: Boolean = false,
    val isDeletedForEveryone: Boolean = false,
    val translatedText: String? = null
)

enum class ChatType {
    DIRECT, GROUP, CHANNEL, COMMUNITY
}

data class Chat(
    val id: String,
    val name: String,
    val avatarUrl: String = "",
    val type: ChatType = ChatType.DIRECT,
    val lastMessage: String = "",
    val lastMessageTime: String = "12:00",
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val membersCount: Int = 2,
    val isVerifiedChannel: Boolean = false,
    val isOnline: Boolean = false
)

data class Story(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val mediaUrl: String = "",
    val textContent: String = "",
    val timestamp: String = "2h ago",
    val isSeen: Boolean = false,
    val viewersCount: Int = 42
)

data class CallSession(
    val id: String,
    val partnerName: String,
    val partnerAvatar: String,
    val isVideo: Boolean,
    val isIncoming: Boolean,
    val duration: String = "00:00",
    val isConnected: Boolean = true,
    val isMuted: Boolean = false,
    val isVideoOn: Boolean = true,
    val isScreenSharing: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val isFrontCamera: Boolean = true,
    val isNoiseCancellationOn: Boolean = true
)

data class CallLogItem(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val type: String,
    val time: String,
    val duration: String,
    val isVideo: Boolean,
    val isHd: Boolean = true
)

data class MarketplaceItem(
    val id: String,
    val sellerId: String,
    val sellerName: String,
    val sellerAvatar: String,
    val sellerPhone: String = "+216 22 999 888",
    val title: String,
    val description: String,
    val price: Double,
    val currency: String = "TND",
    val location: String,
    val category: String,
    val images: List<String>,
    val postedTime: String = "Just now",
    val isSold: Boolean = false,
    val isMine: Boolean = false
)

data class MarketplaceNotification(
    val id: String,
    val friendName: String,
    val friendAvatar: String,
    val productTitle: String,
    val productPrice: String,
    val productId: String,
    val timestamp: String = "Just now"
)
