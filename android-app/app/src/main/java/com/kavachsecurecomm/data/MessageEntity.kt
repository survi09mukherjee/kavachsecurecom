package com.kavachsecurecomm.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val receiverId: String,
    val groupId: String?,
    val plaintextPayload: String, // Since DB is encrypted by SQLCipher, we store plaintext here natively
    val timestamp: Long,
    val isRead: Boolean = false,
    val expiresAt: Long? = null // For Auto-Delete OpSec feature
)
