package com.example.data.model

enum class ChatType {
    PRIVATE, GROUP, CHANNEL, COMMUNITY, AI
}

enum class MessageType {
    TEXT, IMAGE, AUDIO, PDF, DOC, CODE, CALL_LOG, STICKER
}

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ
}

enum class UserRole {
    USER, MODERATOR, ADMIN
}

data class User(
    val id: String = "u_me",
    val email: String = "user@whisper.sec",
    val username: String = "SecurityCommander",
    val avatarUrl: String = "",
    val bio: String = "🔒 E2E Encrypted & Argon2 Protected",
    val role: UserRole = UserRole.ADMIN,
    val is2FAEnabled: Boolean = true,
    val isOnline: Boolean = true,
    val lastSeen: String = "Just now",
    val activeDevicesCount: Int = 3
)

data class Chat(
    val id: String,
    val name: String,
    val avatarUrl: String = "",
    val type: ChatType = ChatType.PRIVATE,
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isStarred: Boolean = false,
    val isE2EE: Boolean = true,
    val membersCount: Int = 2,
    val channelDescription: String = "",
    val isOnline: Boolean = true,
    val lastSeen: String = "Online"
)

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String = "",
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val mediaFileName: String? = null,
    val isSentByMe: Boolean = false,
    val isEncrypted: Boolean = true,
    val status: MessageStatus = MessageStatus.READ,
    val isPinned: Boolean = false,
    val isStarred: Boolean = false,
    val isEdited: Boolean = false,
    val replyToMessageId: String? = null,
    val replyToText: String? = null,
    val cryptoHash: String = "argon2id\$v=19\$m=65536,t=3\$sig_e2e"
)

data class SecurityLog(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String,
    val deviceName: String,
    val ipAddress: String,
    val severity: String, // LOW, MEDIUM, HIGH, CRITICAL
    val status: String    // BLOCKED, ALLOWED, FLAGGED
)

data class DeviceSession(
    val id: String,
    val deviceName: String,
    val deviceType: String, // Mobile, Desktop, Web, Tablet
    val location: String,
    val ipAddress: String,
    val lastActive: String,
    val isCurrent: Boolean = false
)

enum class AiCategory {
    ASSISTANT, CREATIVE, PRODUCTIVITY, CODE, SECURITY, CAREER
}

data class AiFeature(
    val id: String,
    val title: String,
    val description: String,
    val category: AiCategory,
    val iconName: String,
    val defaultPrompt: String
)

data class CallState(
    val activeCallId: String? = null,
    val contactName: String = "",
    val contactAvatar: String = "",
    val isVideo: Boolean = false,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val isScreenSharing: Boolean = false,
    val isNoiseSuppressed: Boolean = true,
    val isConnected: Boolean = false,
    val e2eFingerprint: String = "SHA256:7b:99:a4:e1:12:ef:90:3a"
)

data class AuthUiState(
    val isLoggedIn: Boolean = true, // default logged in for easy testing, toggleable in AuthScreen
    val email: String = "user@whisper.sec",
    val username: String = "SecurityCommander",
    val isOtpRequired: Boolean = false,
    val is2FARequired: Boolean = false,
    val otpCode: String = "",
    val failedAttempts: Int = 0,
    val isAccountLocked: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false
)
