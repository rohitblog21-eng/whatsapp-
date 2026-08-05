package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val text: String,
    val timestamp: Long,
    val type: String,
    val mediaUrl: String?,
    val mediaFileName: String?,
    val isSentByMe: Boolean,
    val isEncrypted: Boolean,
    val status: String,
    val isPinned: Boolean,
    val isStarred: Boolean,
    val isEdited: Boolean,
    val replyToMessageId: String?,
    val replyToText: String?,
    val cryptoHash: String
)

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatarUrl: String,
    val type: String,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val unreadCount: Int,
    val isPinned: Boolean,
    val isStarred: Boolean,
    val isE2EE: Boolean,
    val membersCount: Int,
    val channelDescription: String,
    val isOnline: Boolean,
    val lastSeen: String
)

@Entity(tableName = "security_logs")
data class SecurityLogEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val eventType: String,
    val deviceName: String,
    val ipAddress: String,
    val severity: String,
    val status: String
)

@Entity(tableName = "device_sessions")
data class DeviceSessionEntity(
    @PrimaryKey val id: String,
    val deviceName: String,
    val deviceType: String,
    val location: String,
    val ipAddress: String,
    val lastActive: String,
    val isCurrent: Boolean
)

@Entity(tableName = "ai_history")
data class AiHistoryEntity(
    @PrimaryKey val id: String,
    val featureId: String,
    val prompt: String,
    val response: String,
    val timestamp: Long
)
