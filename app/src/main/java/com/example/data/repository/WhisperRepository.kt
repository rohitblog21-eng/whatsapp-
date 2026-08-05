package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AiHistoryEntity
import com.example.data.local.entity.ChatEntity
import com.example.data.local.entity.DeviceSessionEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.SecurityLogEntity
import com.example.data.model.AiCategory
import com.example.data.model.AiFeature
import com.example.data.model.Chat
import com.example.data.model.ChatType
import com.example.data.model.DeviceSession
import com.example.data.model.Message
import com.example.data.model.MessageStatus
import com.example.data.model.MessageType
import com.example.data.model.SecurityLog
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.remote.GeminiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class WhisperRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val messageDao = db.messageDao()
    private val chatDao = db.chatDao()
    private val securityDao = db.securityDao()
    private val aiHistoryDao = db.aiHistoryDao()

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val existingChats = chatDao.getAllChats().first()
        if (existingChats.isEmpty()) {
            val now = System.currentTimeMillis()

            val defaultChats = listOf(
                ChatEntity(
                    id = "chat_ai",
                    name = "Whisper AI Assistant 🤖",
                    avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
                    type = ChatType.AI.name,
                    lastMessage = "I am ready to help you with code, documents, translations, or security tasks!",
                    lastMessageTimestamp = now - 1000 * 60 * 2,
                    unreadCount = 1,
                    isPinned = true,
                    isStarred = true,
                    isE2EE = true,
                    membersCount = 2,
                    channelDescription = "AI Assistant powered by Gemini 3.5 & E2E Security",
                    isOnline = true,
                    lastSeen = "Always Online"
                ),
                ChatEntity(
                    id = "chat_alex",
                    name = "Alex Rivera (Lead SecOps)",
                    avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                    type = ChatType.PRIVATE.name,
                    lastMessage = "The Argon2 hash rotation complete for production servers 🔒",
                    lastMessageTimestamp = now - 1000 * 60 * 15,
                    unreadCount = 2,
                    isPinned = true,
                    isStarred = false,
                    isE2EE = true,
                    membersCount = 2,
                    channelDescription = "",
                    isOnline = true,
                    lastSeen = "Online"
                ),
                ChatEntity(
                    id = "chat_dev_group",
                    name = "Dev Team Alpha 🚀",
                    avatarUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=150",
                    type = ChatType.GROUP.name,
                    lastMessage = "Sophia: Code review merged. Release tag v2.4.0 is live!",
                    lastMessageTimestamp = now - 1000 * 60 * 45,
                    unreadCount = 0,
                    isPinned = false,
                    isStarred = false,
                    isE2EE = true,
                    membersCount = 12,
                    channelDescription = "Core Mobile & E2EE Engineering Team",
                    isOnline = true,
                    lastSeen = "12 Members"
                ),
                ChatEntity(
                    id = "chat_channel_sec",
                    name = "CyberSec Threat Alerts 📢",
                    avatarUrl = "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=150",
                    type = ChatType.CHANNEL.name,
                    lastMessage = "Zero-day mitigation patch deployed for OpenSSL 3.2.",
                    lastMessageTimestamp = now - 1000 * 60 * 120,
                    unreadCount = 5,
                    isPinned = false,
                    isStarred = false,
                    isE2EE = true,
                    membersCount = 1420,
                    channelDescription = "Official Security Bulletins & Vulnerability Reports",
                    isOnline = false,
                    lastSeen = "1.4k Subscribers"
                ),
                ChatEntity(
                    id = "chat_community_sec",
                    name = "Security Guild Community 🛡️",
                    avatarUrl = "https://images.unsplash.com/photo-1510511459019-5dda7724fd87?w=150",
                    type = ChatType.COMMUNITY.name,
                    lastMessage = "Welcome new members! Read our E2E guidelines.",
                    lastMessageTimestamp = now - 1000 * 60 * 300,
                    unreadCount = 0,
                    isPinned = false,
                    isStarred = false,
                    isE2EE = true,
                    membersCount = 85,
                    channelDescription = "Community of Cryptography & OWASP Enthusiasts",
                    isOnline = true,
                    lastSeen = "5 Groups Included"
                )
            )
            chatDao.insertChats(defaultChats)

            val defaultMessages = listOf(
                MessageEntity(
                    id = "m1",
                    chatId = "chat_alex",
                    senderId = "alex",
                    senderName = "Alex Rivera",
                    senderAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                    text = "Hey! Have you checked the latest OWASP rate-limiting logs?",
                    timestamp = now - 1000 * 60 * 30,
                    type = MessageType.TEXT.name,
                    mediaUrl = null,
                    mediaFileName = null,
                    isSentByMe = false,
                    isEncrypted = true,
                    status = MessageStatus.READ.name,
                    isPinned = false,
                    isStarred = false,
                    isEdited = false,
                    replyToMessageId = null,
                    replyToText = null,
                    cryptoHash = "argon2id\$v=19\$m=65536,t=3\$hash_019a"
                ),
                MessageEntity(
                    id = "m2",
                    chatId = "chat_alex",
                    senderId = "u_me",
                    senderName = "Me",
                    senderAvatar = "",
                    text = "Yes, Argon2 key derivation was enforced and suspicious IPs were rate-limited.",
                    timestamp = now - 1000 * 60 * 25,
                    type = MessageType.TEXT.name,
                    mediaUrl = null,
                    mediaFileName = null,
                    isSentByMe = true,
                    isEncrypted = true,
                    status = MessageStatus.READ.name,
                    isPinned = false,
                    isStarred = true,
                    isEdited = false,
                    replyToMessageId = "m1",
                    replyToText = "Hey! Have you checked the latest OWASP rate-limiting logs?",
                    cryptoHash = "argon2id\$v=19\$m=65536,t=3\$hash_019b"
                ),
                MessageEntity(
                    id = "m3",
                    chatId = "chat_alex",
                    senderId = "alex",
                    senderName = "Alex Rivera",
                    senderAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                    text = "The Argon2 hash rotation complete for production servers 🔒",
                    timestamp = now - 1000 * 60 * 15,
                    type = MessageType.TEXT.name,
                    mediaUrl = null,
                    mediaFileName = null,
                    isSentByMe = false,
                    isEncrypted = true,
                    status = MessageStatus.DELIVERED.name,
                    isPinned = true,
                    isStarred = false,
                    isEdited = false,
                    replyToMessageId = null,
                    replyToText = null,
                    cryptoHash = "argon2id\$v=19\$m=65536,t=3\$hash_019c"
                ),
                // AI Chat initial messages
                MessageEntity(
                    id = "m_ai1",
                    chatId = "chat_ai",
                    senderId = "ai",
                    senderName = "Whisper AI",
                    senderAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
                    text = "Hello Commander! I am your AI Assistant. You can ask me to debug code, translate messages, summarize PDFs, write emails, or analyze security logs.",
                    timestamp = now - 1000 * 60 * 2,
                    type = MessageType.TEXT.name,
                    mediaUrl = null,
                    mediaFileName = null,
                    isSentByMe = false,
                    isEncrypted = true,
                    status = MessageStatus.READ.name,
                    isPinned = true,
                    isStarred = true,
                    isEdited = false,
                    replyToMessageId = null,
                    replyToText = null,
                    cryptoHash = "argon2id\$v=19\$m=65536,t=3\$hash_ai1"
                )
            )
            messageDao.insertMessages(defaultMessages)

            // Seed Security Logs
            val defaultSecurityLogs = listOf(
                SecurityLogEntity(
                    id = "sec_1",
                    timestamp = now - 1000 * 60 * 10,
                    eventType = "Argon2 KDF Re-Keying",
                    deviceName = "Android Pixel 8 Pro",
                    ipAddress = "192.168.1.104",
                    severity = "LOW",
                    status = "ALLOWED"
                ),
                SecurityLogEntity(
                    id = "sec_2",
                    timestamp = now - 1000 * 60 * 55,
                    eventType = "Failed Login Attempt (Account Lock Triggered)",
                    deviceName = "Unknown Linux Client",
                    ipAddress = "185.220.101.5",
                    severity = "CRITICAL",
                    status = "BLOCKED"
                ),
                SecurityLogEntity(
                    id = "sec_3",
                    timestamp = now - 1000 * 60 * 180,
                    eventType = "2FA Email OTP Verification",
                    deviceName = "MacBook Pro M3",
                    ipAddress = "192.168.1.12",
                    severity = "LOW",
                    status = "ALLOWED"
                ),
                SecurityLogEntity(
                    id = "sec_4",
                    timestamp = now - 1000 * 60 * 400,
                    eventType = "Rate Limit Exceeded (XSS Protection)",
                    deviceName = "Web Browser Engine",
                    ipAddress = "104.28.19.88",
                    severity = "HIGH",
                    status = "FLAGGED"
                )
            )
            securityDao.insertSecurityLogs(defaultSecurityLogs)

            // Seed Device Sessions
            val defaultDeviceSessions = listOf(
                DeviceSessionEntity(
                    id = "dev_1",
                    deviceName = "Android Studio Emulator (Current)",
                    deviceType = "Mobile",
                    location = "San Francisco, CA, USA",
                    ipAddress = "192.168.1.104",
                    lastActive = "Active Now",
                    isCurrent = true
                ),
                DeviceSessionEntity(
                    id = "dev_2",
                    deviceName = "MacBook Pro M3 Max",
                    deviceType = "Desktop",
                    location = "San Francisco, CA, USA",
                    ipAddress = "192.168.1.12",
                    lastActive = "2 hours ago",
                    isCurrent = false
                ),
                DeviceSessionEntity(
                    id = "dev_3",
                    deviceName = "iPad Pro 12.9 (Whisper Web)",
                    deviceType = "Tablet",
                    location = "New York, NY, USA",
                    ipAddress = "72.229.28.18",
                    lastActive = "Yesterday",
                    isCurrent = false
                )
            )
            securityDao.insertDeviceSessions(defaultDeviceSessions)
        }
    }

    // Chat flows
    fun getAllChats(): Flow<List<Chat>> = chatDao.getAllChats().map { list ->
        list.map { it.toModel() }
    }

    fun getMessagesForChat(chatId: String): Flow<List<Message>> = messageDao.getMessagesForChat(chatId).map { list ->
        list.map { it.toModel() }
    }

    fun getSecurityLogs(): Flow<List<SecurityLog>> = securityDao.getSecurityLogs().map { list ->
        list.map { it.toModel() }
    }

    fun getDeviceSessions(): Flow<List<DeviceSession>> = securityDao.getDeviceSessions().map { list ->
        list.map { it.toModel() }
    }

    suspend fun sendMessage(
        chatId: String,
        text: String,
        type: MessageType = MessageType.TEXT,
        mediaUrl: String? = null,
        mediaFileName: String? = null,
        replyToId: String? = null,
        replyToText: String? = null
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val msgId = "msg_" + UUID.randomUUID().toString().take(8)
        val hash = "argon2id\$v=19\$m=65536,t=3\$" + UUID.randomUUID().toString().take(12)

        val entity = MessageEntity(
            id = msgId,
            chatId = chatId,
            senderId = "u_me",
            senderName = "Me",
            senderAvatar = "",
            text = text,
            timestamp = now,
            type = type.name,
            mediaUrl = mediaUrl,
            mediaFileName = mediaFileName,
            isSentByMe = true,
            isEncrypted = true,
            status = MessageStatus.SENT.name,
            isPinned = false,
            isStarred = false,
            isEdited = false,
            replyToMessageId = replyToId,
            replyToText = replyToText,
            cryptoHash = hash
        )
        messageDao.insertMessage(entity)

        val lastMsgPreview = when (type) {
            MessageType.IMAGE -> "📷 Image shared"
            MessageType.AUDIO -> "🎙️ Voice note (0:14)"
            MessageType.PDF -> "📄 $mediaFileName"
            MessageType.CODE -> "💻 Code snippet"
            else -> text
        }
        chatDao.updateLastMessage(chatId, lastMsgPreview, now)

        // If it's the AI chat, auto generate AI response!
        if (chatId == "chat_ai" || text.startsWith("@AI")) {
            val promptText = if (text.startsWith("@AI")) text.removePrefix("@AI").trim() else text
            val aiResponse = GeminiService.generateAiResponse(promptText)
            val aiMsgId = "ai_" + UUID.randomUUID().toString().take(8)
            val aiEntity = MessageEntity(
                id = aiMsgId,
                chatId = chatId,
                senderId = "ai",
                senderName = "Whisper AI",
                senderAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
                text = aiResponse,
                timestamp = System.currentTimeMillis() + 500,
                type = MessageType.TEXT.name,
                mediaUrl = null,
                mediaFileName = null,
                isSentByMe = false,
                isEncrypted = true,
                status = MessageStatus.READ.name,
                isPinned = false,
                isStarred = false,
                isEdited = false,
                replyToMessageId = msgId,
                replyToText = text.take(60),
                cryptoHash = "argon2id\$v=19\$m=65536,t=3\$ai_resp_sig"
            )
            messageDao.insertMessage(aiEntity)
            chatDao.updateLastMessage(chatId, aiResponse.take(60) + "...", System.currentTimeMillis() + 500)

            // Add to AI history
            aiHistoryDao.insertAiHistory(
                AiHistoryEntity(
                    id = UUID.randomUUID().toString(),
                    featureId = "chat_ai",
                    prompt = promptText,
                    response = aiResponse,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun executeAiTool(feature: AiFeature, userInput: String): String = withContext(Dispatchers.IO) {
        val fullPrompt = "${feature.defaultPrompt}\n\nUser Input:\n$userInput"
        val response = GeminiService.generateAiResponse(fullPrompt)
        aiHistoryDao.insertAiHistory(
            AiHistoryEntity(
                id = UUID.randomUUID().toString(),
                featureId = feature.id,
                prompt = userInput,
                response = response,
                timestamp = System.currentTimeMillis()
            )
        )
        response
    }

    suspend fun deleteMessage(messageId: String) = withContext(Dispatchers.IO) {
        messageDao.deleteMessage(messageId)
    }

    suspend fun toggleStarMessage(messageId: String, currentStarred: Boolean) = withContext(Dispatchers.IO) {
        messageDao.updateStarStatus(messageId, !currentStarred)
    }

    suspend fun togglePinMessage(messageId: String, currentPinned: Boolean) = withContext(Dispatchers.IO) {
        messageDao.updatePinStatus(messageId, !currentPinned)
    }

    suspend fun editMessage(messageId: String, newText: String) = withContext(Dispatchers.IO) {
        messageDao.updateMessageText(messageId, newText)
    }

    suspend fun revokeDeviceSession(sessionId: String) = withContext(Dispatchers.IO) {
        securityDao.revokeDeviceSession(sessionId)
        securityDao.insertSecurityLog(
            SecurityLogEntity(
                id = "sec_rev_" + System.currentTimeMillis(),
                timestamp = System.currentTimeMillis(),
                eventType = "Remote Device Session Revoked",
                deviceName = "Revoked Device ($sessionId)",
                ipAddress = "192.168.1.1",
                severity = "MEDIUM",
                status = "ALLOWED"
            )
        )
    }

    suspend fun clearUnread(chatId: String) = withContext(Dispatchers.IO) {
        chatDao.clearUnread(chatId)
    }

    // Data mapping helpers
    private fun ChatEntity.toModel(): Chat = Chat(
        id = id,
        name = name,
        avatarUrl = avatarUrl,
        type = try { ChatType.valueOf(type) } catch (e: Exception) { ChatType.PRIVATE },
        lastMessage = lastMessage,
        lastMessageTimestamp = lastMessageTimestamp,
        unreadCount = unreadCount,
        isPinned = isPinned,
        isStarred = isStarred,
        isE2EE = isE2EE,
        membersCount = membersCount,
        channelDescription = channelDescription,
        isOnline = isOnline,
        lastSeen = lastSeen
    )

    private fun MessageEntity.toModel(): Message = Message(
        id = id,
        chatId = chatId,
        senderId = senderId,
        senderName = senderName,
        senderAvatar = senderAvatar,
        text = text,
        timestamp = timestamp,
        type = try { MessageType.valueOf(type) } catch (e: Exception) { MessageType.TEXT },
        mediaUrl = mediaUrl,
        mediaFileName = mediaFileName,
        isSentByMe = isSentByMe,
        isEncrypted = isEncrypted,
        status = try { MessageStatus.valueOf(status) } catch (e: Exception) { MessageStatus.READ },
        isPinned = isPinned,
        isStarred = isStarred,
        isEdited = isEdited,
        replyToMessageId = replyToMessageId,
        replyToText = replyToText,
        cryptoHash = cryptoHash
    )

    private fun SecurityLogEntity.toModel(): SecurityLog = SecurityLog(
        id = id,
        timestamp = timestamp,
        eventType = eventType,
        deviceName = deviceName,
        ipAddress = ipAddress,
        severity = severity,
        status = status
    )

    private fun DeviceSessionEntity.toModel(): DeviceSession = DeviceSession(
        id = id,
        deviceName = deviceName,
        deviceType = deviceType,
        location = location,
        ipAddress = ipAddress,
        lastActive = lastActive,
        isCurrent = isCurrent
    )
}
