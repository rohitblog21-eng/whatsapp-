package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AiHistoryEntity
import com.example.data.local.entity.ChatEntity
import com.example.data.local.entity.DeviceSessionEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.SecurityLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)

    @Query("UPDATE messages SET isStarred = :isStarred WHERE id = :messageId")
    suspend fun updateStarStatus(messageId: String, isStarred: Boolean)

    @Query("UPDATE messages SET isPinned = :isPinned WHERE id = :messageId")
    suspend fun updatePinStatus(messageId: String, isPinned: Boolean)

    @Query("UPDATE messages SET text = :newText, isEdited = 1 WHERE id = :messageId")
    suspend fun updateMessageText(messageId: String, newText: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY isPinned DESC, lastMessageTimestamp DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<ChatEntity>)

    @Query("UPDATE chats SET lastMessage = :lastMsg, lastMessageTimestamp = :ts WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: String, lastMsg: String, ts: Long)

    @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
    suspend fun clearUnread(chatId: String)
}

@Dao
interface SecurityDao {
    @Query("SELECT * FROM security_logs ORDER BY timestamp DESC")
    fun getSecurityLogs(): Flow<List<SecurityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityLog(log: SecurityLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityLogs(logs: List<SecurityLogEntity>)

    @Query("SELECT * FROM device_sessions ORDER BY isCurrent DESC")
    fun getDeviceSessions(): Flow<List<DeviceSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceSession(session: DeviceSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceSessions(sessions: List<DeviceSessionEntity>)

    @Query("DELETE FROM device_sessions WHERE id = :sessionId")
    suspend fun revokeDeviceSession(sessionId: String)
}

@Dao
interface AiHistoryDao {
    @Query("SELECT * FROM ai_history ORDER BY timestamp DESC")
    fun getAiHistory(): Flow<List<AiHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiHistory(item: AiHistoryEntity)
}
