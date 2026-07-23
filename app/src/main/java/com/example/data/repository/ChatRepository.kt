package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import com.example.data.remote.GeminiAiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(
    private val database: AppDatabase,
    private val aiService: GeminiAiService
) {
    private val messageDao = database.messageDao()
    private val chatDao = database.chatDao()
    private val userDao = database.userDao()

    val allChats: Flow<List<Chat>> = chatDao.getAllChats().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getMessages(chatId: String): Flow<List<Message>> =
        messageDao.getMessagesForChat(chatId).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun sendMessage(
        chatId: String,
        senderId: String,
        senderName: String,
        content: String,
        type: MessageType = MessageType.TEXT,
        mediaUrl: String = "",
        replyToId: String? = null,
        replyToText: String? = null
    ) {
        val messageId = "msg_${System.currentTimeMillis()}"
        val entity = MessageEntity(
            id = messageId,
            chatId = chatId,
            senderId = senderId,
            senderName = senderName,
            senderAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
            content = content,
            type = type.name,
            mediaUrl = mediaUrl,
            timestamp = System.currentTimeMillis(),
            isRead = true,
            isPinned = false,
            replyToId = replyToId,
            replyToText = replyToText,
            isEdited = false,
            isDeletedForEveryone = false,
            translatedText = null
        )
        messageDao.insertMessage(entity)
        chatDao.updateLastMessage(chatId, content, "Just now")
    }

    suspend fun togglePinMessage(messageId: String, isPinned: Boolean) {
        messageDao.updatePinnedStatus(messageId, isPinned)
    }

    suspend fun editMessage(messageId: String, newContent: String) {
        messageDao.editMessage(messageId, newContent)
    }

    suspend fun deleteForEveryone(messageId: String) {
        messageDao.deleteForEveryone(messageId)
    }

    suspend fun translateMessage(messageId: String, content: String, targetLang: String) {
        val translated = aiService.translateMessage(content, targetLang)
        messageDao.updateTranslation(messageId, translated)
    }

    suspend fun seedInitialData() {
        // Seed initial chats if empty
        val initialChats = listOf(
            ChatEntity(
                id = "ai_assistant",
                name = "Gemini AI Assistant 🤖",
                avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
                type = ChatType.DIRECT.name,
                lastMessage = "Aychak! How can I assist you in Derja, French, or English today?",
                lastMessageTime = "10:30 AM",
                unreadCount = 0,
                isPinned = true,
                isMuted = false,
                membersCount = 2,
                isVerifiedChannel = true,
                isOnline = true
            ),
            ChatEntity(
                id = "chat_youssef",
                name = "Youssef Chahed 🇹🇳",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                type = ChatType.DIRECT.name,
                lastMessage = "Labes bro, ready for coffee in La Marsa at 4 PM?",
                lastMessageTime = "09:15 AM",
                unreadCount = 2,
                isPinned = true,
                isMuted = false,
                membersCount = 2,
                isVerifiedChannel = false,
                isOnline = true
            ),
            ChatEntity(
                id = "chat_cyrine",
                name = "Cyrine Medenine",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                type = ChatType.DIRECT.name,
                lastMessage = "Sahtek! The photos from Djerba look incredible 🌴",
                lastMessageTime = "Yesterday",
                unreadCount = 0,
                isPinned = false,
                isMuted = false,
                membersCount = 2,
                isVerifiedChannel = false,
                isOnline = false
            ),
            ChatEntity(
                id = "group_tech_tn",
                name = "Tunisie Tech & AI 🇹🇳",
                avatarUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=150",
                type = ChatType.GROUP.name,
                lastMessage = "Mehdi: Check out the new Android Compose release!",
                lastMessageTime = "Yesterday",
                unreadCount = 5,
                isPinned = false,
                isMuted = false,
                membersCount = 342,
                isVerifiedChannel = false,
                isOnline = true
            ),
            ChatEntity(
                id = "channel_actu_tn",
                name = "Tunisie Actu News 📣",
                avatarUrl = "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=150",
                type = ChatType.CHANNEL.name,
                lastMessage = "Breaking: Startup Act 2.0 approved in parliament with new incentives.",
                lastMessageTime = "Jul 22",
                unreadCount = 0,
                isPinned = false,
                isMuted = false,
                membersCount = 48200,
                isVerifiedChannel = true,
                isOnline = true
            )
        )
        chatDao.insertChats(initialChats)

        // Seed initial messages for Youssef
        val youssefMessages = listOf(
            MessageEntity(
                id = "m1",
                chatId = "chat_youssef",
                senderId = "youssef",
                senderName = "Youssef Chahed",
                senderAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                content = "Aslema! Labes 3lik?",
                type = MessageType.TEXT.name,
                mediaUrl = "",
                timestamp = System.currentTimeMillis() - 3600000,
                isRead = true,
                isPinned = false,
                replyToId = null,
                replyToText = null,
                isEdited = false,
                isDeletedForEveryone = false,
                translatedText = null
            ),
            MessageEntity(
                id = "m2",
                chatId = "chat_youssef",
                senderId = "me",
                senderName = "Me",
                senderAvatar = "",
                content = "Hamdullah! Everything going great with Zahrouni Chat app development.",
                type = MessageType.TEXT.name,
                mediaUrl = "",
                timestamp = System.currentTimeMillis() - 1800000,
                isRead = true,
                isPinned = false,
                replyToId = null,
                replyToText = null,
                isEdited = false,
                isDeletedForEveryone = false,
                translatedText = null
            ),
            MessageEntity(
                id = "m3",
                chatId = "chat_youssef",
                senderId = "youssef",
                senderName = "Youssef Chahed",
                senderAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                content = "Labes bro, ready for coffee in La Marsa at 4 PM?",
                type = MessageType.TEXT.name,
                mediaUrl = "",
                timestamp = System.currentTimeMillis() - 900000,
                isRead = false,
                isPinned = true,
                replyToId = null,
                replyToText = null,
                isEdited = false,
                isDeletedForEveryone = false,
                translatedText = null
            )
        )
        messageDao.insertMessages(youssefMessages)

        // Seed initial messages for Gemini AI
        val aiMessages = listOf(
            MessageEntity(
                id = "m_ai1",
                chatId = "ai_assistant",
                senderId = "ai",
                senderName = "Gemini AI Assistant",
                senderAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
                content = "Aslema! I am your AI Assistant for Zahrouni Chat. Ask me anything, translate Tunisian Derja, summarize chats, or generate smart replies!",
                type = MessageType.TEXT.name,
                mediaUrl = "",
                timestamp = System.currentTimeMillis() - 7200000,
                isRead = true,
                isPinned = true,
                replyToId = null,
                replyToText = null,
                isEdited = false,
                isDeletedForEveryone = false,
                translatedText = null
            )
        )
        messageDao.insertMessages(aiMessages)
    }

    suspend fun addContactByPhoneOrUsername(query: String): String {
        val cleanQuery = query.trim()
        val sanitizedId = "chat_" + cleanQuery.replace(Regex("[^a-zA-Z0-9]"), "_").lowercase()

        val isPhone = cleanQuery.startsWith("+") || cleanQuery.all { it.isDigit() || it == ' ' || it == '+' || it == '-' }
        val displayName = if (isPhone) {
            "Contact ($cleanQuery)"
        } else {
            cleanQuery.removePrefix("@").replace("_", " ").split(" ")
                .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
        }

        val avatar = if (cleanQuery.contains("sousse", ignoreCase = true) || cleanQuery.contains("amine", ignoreCase = true)) {
            "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150"
        } else if (cleanQuery.contains("medenine", ignoreCase = true) || cleanQuery.contains("cyrine", ignoreCase = true)) {
            "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150"
        } else {
            "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"
        }

        val newChat = ChatEntity(
            id = sanitizedId,
            name = displayName,
            avatarUrl = avatar,
            type = ChatType.DIRECT.name,
            lastMessage = "Added to contacts",
            lastMessageTime = "Just now",
            unreadCount = 0,
            isPinned = false,
            isMuted = false,
            membersCount = 2,
            isVerifiedChannel = false,
            isOnline = true
        )
        chatDao.insertChat(newChat)

        val initMessage = MessageEntity(
            id = "msg_${System.currentTimeMillis()}",
            chatId = sanitizedId,
            senderId = "system",
            senderName = "Zahrouni Chat 🇹🇳",
            senderAvatar = avatar,
            content = "👋 You added $displayName ($cleanQuery) to your contacts. Say Aslema!",
            type = MessageType.TEXT.name,
            mediaUrl = "",
            timestamp = System.currentTimeMillis(),
            isRead = true,
            isPinned = false,
            replyToId = null,
            replyToText = null,
            isEdited = false,
            isDeletedForEveryone = false,
            translatedText = null
        )
        messageDao.insertMessage(initMessage)

        return sanitizedId
    }

    private fun ChatEntity.toDomain(): Chat {
        return Chat(
            id = id,
            name = name,
            avatarUrl = avatarUrl,
            type = ChatType.valueOf(type),
            lastMessage = lastMessage,
            lastMessageTime = lastMessageTime,
            unreadCount = unreadCount,
            isPinned = isPinned,
            isMuted = isMuted,
            membersCount = membersCount,
            isVerifiedChannel = isVerifiedChannel,
            isOnline = isOnline
        )
    }

    private fun MessageEntity.toDomain(): Message {
        return Message(
            id = id,
            chatId = chatId,
            senderId = senderId,
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = content,
            type = MessageType.valueOf(type),
            mediaUrl = mediaUrl,
            timestamp = timestamp,
            isRead = isRead,
            isPinned = isPinned,
            replyToId = replyToId,
            replyToText = replyToText,
            isEdited = isEdited,
            isDeletedForEveryone = isDeletedForEveryone,
            translatedText = translatedText
        )
    }
}
