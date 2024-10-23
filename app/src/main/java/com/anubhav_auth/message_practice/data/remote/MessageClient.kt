package com.anubhav_auth.message_practice.data.remote

import com.anubhav_auth.message_practice.data.model.Message
import com.anubhav_auth.message_practice.data.model.MessageStatusUpdates
import com.anubhav_auth.type.MessageStatus
import kotlinx.coroutines.flow.Flow

interface MessageClient {
    suspend fun subscribeToTopic(topic: String): Flow<Message?>
    suspend fun subscribeToMessageStatusUpdates(topic: String): Flow<MessageStatusUpdates?>
    suspend fun sendMessage(message: Message): Message?
    suspend fun statusUpdate(message: MessageStatusUpdates, status: MessageStatus): MessageStatusUpdates?
}