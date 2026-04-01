package com.kavachsecurecomm.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    // Flow provides real-time updates directly to Jetpack Compose UI
    @Query("SELECT * FROM messages WHERE receiverId = :userId OR senderId = :userId ORDER BY timestamp ASC")
    fun getChatHistoryAsFlow(userId: String): Flow<List<MessageEntity>>

    @Query("DELETE FROM messages WHERE expiresAt IS NOT NULL AND expiresAt <= :currentTime")
    suspend fun clearExpiredMessages(currentTime: Long)
}
