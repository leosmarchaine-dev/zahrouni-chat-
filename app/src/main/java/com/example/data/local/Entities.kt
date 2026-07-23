package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val content: String,
    val type: String, // TEXT, IMAGE, VOICE_NOTE, etc.
    val mediaUrl: String,
    val timestamp: Long,
    val isRead: Boolean,
    val isPinned: Boolean,
    val replyToId: String?,
    val replyToText: String?,
    val isEdited: Boolean,
    val isDeletedForEveryone: Boolean,
    val translatedText: String?
)

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatarUrl: String,
    val type: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int,
    val isPinned: Boolean,
    val isMuted: Boolean,
    val membersCount: Int,
    val isVerifiedChannel: Boolean,
    val isOnline: Boolean
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val username: String,
    val phoneNumber: String,
    val email: String,
    val avatarUrl: String,
    val coverUrl: String,
    val bio: String,
    val gender: String,
    val birthday: String,
    val country: String,
    val city: String,
    val isOnline: Boolean,
    val lastSeen: String,
    val isVerified: Boolean,
    val isPremium: Boolean,
    val role: String
)
