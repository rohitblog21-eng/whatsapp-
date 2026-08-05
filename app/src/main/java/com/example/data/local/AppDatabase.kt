package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AiHistoryDao
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.MessageDao
import com.example.data.local.dao.SecurityDao
import com.example.data.local.entity.AiHistoryEntity
import com.example.data.local.entity.ChatEntity
import com.example.data.local.entity.DeviceSessionEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.SecurityLogEntity

@Database(
    entities = [
        MessageEntity::class,
        ChatEntity::class,
        SecurityLogEntity::class,
        DeviceSessionEntity::class,
        AiHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun chatDao(): ChatDao
    abstract fun securityDao(): SecurityDao
    abstract fun aiHistoryDao(): AiHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "whisper_secure_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
