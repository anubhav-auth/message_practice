package com.anubhav_auth.message_practice.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anubhav_auth.type.MessageStatus

@Entity
data class Message(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val topic: String,
    val content: String,
    val sender: String,
    val receiver: String,
    val status: MessageStatus,
    val sentAt: String,
    val deliveredAt: String,
    val readAt: String,
)
